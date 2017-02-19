package pub.edholm.tacobot.Listeners

import org.pircbotx.hooks.ListenerAdapter
import org.pircbotx.hooks.events.MessageEvent
import pub.edholm.tacobot.logger
import pub.edholm.tacobot.movieapi.Imdb

class MovieListener : ListenerAdapter() {
    val LOG by logger()
    override fun onMessage(event: MessageEvent?) {
        val msg = event?.message ?: return
        if (!msg.startsWith("TacoBot:")) return
        // TODO: Ignore messages from ourselves

        val query = msg.substringAfter(":").trim()
        LOG.info("IMDb request for '$query' from ${event?.user?.nick}")


        val imdbResult = Imdb.bestMatch(query)
        if (imdbResult == null) {
            LOG.info("Nothing found for '$query'")
            event?.respond("sorry, couldn't find anything matching '$query'")
            return
        }

        val details = Imdb.scrapeTitleDetails(imdbResult) ?: return
        LOG.info("Found details: $details")
        LOG.info("Replying to ${event?.user?.nick}")
        event?.respondWith(details.toPrettyString())
        event?.respondWith("Runtime: ${details.runtime.toHours()}h ${details.runtime.toMinutes() % 60}m | ${imdbResult.toUrl()}")
    }
}