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
        val msg = event?.message ?: return

        val botNick = event?.getBot<PircBotX>()?.nick
        if (!msg.startsWith("$botNick:")) return

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
        event?.respondWith("Runtime: ${parseRuntime(details.runtime)} | ${imdbResult.toUrl()}")
    }

    private fun parseRuntime(runtime: Duration): String {
        val hours = if (runtime.toHours() > 0) "${runtime.toHours()}h " else ""
        val minutes = "${runtime.toMinutes() % 60}m"
        return "$hours$minutes"
    }
}