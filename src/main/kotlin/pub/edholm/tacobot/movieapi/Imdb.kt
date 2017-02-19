package pub.edholm.tacobot.movieapi

import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import pub.edholm.tacobot.logger
import java.time.Duration
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object Imdb {
    val LOG by logger()

    /**
     * Return the id of the first best possible match for the supplied query, or null if nothing is found.
     */
    fun bestMatch(query: String): Id? {
        LOG.info("Searching for best match: $query")
        val params = mapOf("json" to "1",
                "nr" to "1",
                "tt" to "on",
                "q" to query)
        val request = khttp.get("http://www.imdb.com/xml/find", params = params)
        val json = request.jsonObject

        var id: Id? = null
        if (json.has("title_popular")) {
            id = getFirstIdFromArray("title_popular", json)
        } else if (json.has("title_exact")) {
            id = getFirstIdFromArray("title_exact", json)
        } else if (json.has("title_approx")) {
            id = getFirstIdFromArray("title_approx", json)
        }
        LOG.info("Found id: $id")
        return id
    }

    fun scrapeTitleDetails(id: Id): Details? {
        LOG.info("Scraping ${id.toUrl()}")
        val document: Document?
        try {
            document = Jsoup.connect(id.toUrl()).userAgent("Mozilla/5.0 (X11; Linux x86_64; rv:51.0) Gecko/20100101 Firefox/51.0").get()
        } catch(e: Exception) {
            LOG.error("Unable to scrape IMDb: ", e)
            return null
        }

        val title = document.select(".title_wrapper > h1:nth-child(1)").text().substringBeforeLast("(").trim()
        val year = document.select("#titleYear > a:nth-child(1)").text()
        val summary = document.select(".summary_text[itemprop=description]").text()
        val rating = document.select(".ratingValue > strong:nth-child(1) > span:nth-child(1)").text()
        val ratingCount = document.select("span.small[itemprop=ratingCount]").text()
        val durationStr = document.select(".subtext > time:nth-child(3)").attr("datetime")
        val runtime = Duration.parse(durationStr)
        val genres = document.select(".itemprop[itemprop=genre]").map(Element::text)
        val datePublishedStr = document.select("meta[itemprop=datePublished]").attr("content")
        val datePublished = LocalDate.parse(datePublishedStr, DateTimeFormatter.ISO_LOCAL_DATE)

        return Details(id = id,
                title = title,
                year = year,
                summary = summary,
                rating = rating,
                ratingCount = ratingCount,
                runtime = runtime,
                genres = genres,
                releaseDate = datePublished)
    }

    private fun getFirstIdFromArray(key: String, json: JSONObject): Id {
        return Id(json.getJSONArray(key).getJSONObject(0).getString("id"))
    }

    data class Details(val id: Id,
                       val title: String,
                       val year: String,
                       val summary: String,
                       val rating: String,
                       val ratingCount: String,
                       val runtime: Duration,
                       val genres: List<String>,
                       val releaseDate: LocalDate) {
        fun toPrettyString(): String {
            return "$title ($year) $rating/10 $genres"
        }
    }

    data class Id(val titleId: String) {
        init {
            if (!titleId.matches(Regex("^tt[0-9]{7}"))) {
                LOG.error("$titleId does not match a valid IMDb title ID")
                throw IllegalArgumentException("Unknown title id format: $titleId")
            }
        }

        fun toUrl(): String {
            return "http://www.imdb.com/title/$titleId"
        }
    }
}
