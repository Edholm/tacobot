package pub.edholm.tacobot.Listeners

import org.pircbotx.PircBotX
import org.pircbotx.hooks.ListenerAdapter
import org.pircbotx.hooks.events.MessageEvent
import pub.edholm.tacobot.logger
import pub.edholm.tacobot.movieapi.Imdb
import java.time.Duration

class MovieListener : ListenerAdapter() {
    val LOG by logger()
    override fun onMessage(event: MessageEvent?) {
        val botNick = event?.getBot<PircBotX>()?.nick
        val msg = event?.message ?: return

        val extractedImdbIds = extractImdbId(msg)
        if (extractedImdbIds.isNotEmpty()) {
            LOG.info("Incoming message contains %d IMDb titles".format(extractedImdbIds.size))
            val toIndex = if (extractedImdbIds.size > 2) 2 else extractedImdbIds.size
            extractedImdbIds.subList(0, toIndex).forEach{ id ->  scrapeImdbAndRespondWithDetails(id, event) }
            if (extractedImdbIds.size > 2) {
                event?.respondWith("... ignoring the rest")
            }
            return
        }

        if (!msg.startsWith("$botNick:")) return

        val query = msg.substringAfter(":").trim()
        LOG.info("IMDb request for '$query' from ${event?.user?.nick}")


        val imdbResult = Imdb.bestMatch(query)
        if (imdbResult == null) {
            LOG.info("Nothing found for '$query'")
            event?.respond("sorry, couldn't find anything matching '$query'")
            return
        }
        scrapeImdbAndRespondWithDetails(imdbResult, event)
    }

    internal fun scrapeImdbAndRespondWithDetails(id: Imdb.Id, event: MessageEvent?) {
        val details = Imdb.scrapeTitleDetails(id)
        if (details == null) {
            LOG.info("Couldn't find any details for id[%s]".format(id.titleId))
            return
        }

        LOG.info("Scraped details: $details")
        LOG.info("Replying to ${event?.user?.nick}")
        event?.respondWith(details.toPrettyString())
        event?.respondWith("Runtime: ${parseRuntime(details.runtime)} | ${id.toUrl()}")
    }

    internal fun extractImdbId(msg: String): List<Imdb.Id> {
        val regex = Regex("tt[0-9]{7}")
        val allMatches = regex.findAll(msg.toLowerCase())
        val ids: List<Imdb.Id> = allMatches.map { m -> Imdb.Id(m.value) }.toList()
        return ids;
    }

    private fun parseRuntime(runtime: Duration): String {
        val hours = if (runtime.toHours() > 0) "${runtime.toHours()}h " else ""
        val minutes = "${runtime.toMinutes() % 60}m"
        return "$hours$minutes"
    }
}