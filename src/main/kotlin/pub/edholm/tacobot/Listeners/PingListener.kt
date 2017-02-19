package pub.edholm.tacobot.Listeners

import org.pircbotx.hooks.ListenerAdapter
import org.pircbotx.hooks.events.MessageEvent
import pub.edholm.tacobot.logger
import java.time.Duration
import java.time.Instant

class PingListener : ListenerAdapter() {
    val LOG by logger()
    var lastPing: Instant = Instant.EPOCH

    private fun ping(pingMsg: String, event: MessageEvent) {
        if (Duration.between(lastPing, Instant.now()).toMinutes() < 2) {
            LOG.info("Too soon between pings, returning insult")
            event.respond("https://goo.gl/s4pJrk")
            lastPing = Instant.now()
            return
        }

        val commaSeparatedNicks = event.channel.usersNicks.filterNot { it -> it == "TacoBot" || it == event.user?.nick }
                .joinToString()
        event.respondChannel("$pingMsg ($commaSeparatedNicks)")
        lastPing = Instant.now()
    }

    override fun onMessage(event: MessageEvent?) {
        val msg = event?.message ?: return

        if (msg.startsWith("!ping")) {
            LOG.info("Received ping request from ${event?.user?.nick}")
            val pingMsg = msg.substringAfter("!ping").trim()
            ping(pingMsg, event!!)
        }
    }
}