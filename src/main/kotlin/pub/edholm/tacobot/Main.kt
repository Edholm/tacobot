package pub.edholm.tacobot

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.DefaultHelpFormatter
import org.pircbotx.Configuration
import org.pircbotx.PircBotX
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import pub.edholm.tacobot.Listeners.MovieListener
import pub.edholm.tacobot.Listeners.PingListener
import javax.net.ssl.SSLSocketFactory

fun Any.logger(): Lazy<Logger> {
    return lazy { LoggerFactory.getLogger(this.javaClass) }
}

fun main(args: Array<String>) {
    val logger = LoggerFactory.getLogger("mainMethod")
    val parsedArgs = Args(ArgParser(args, helpFormatter = DefaultHelpFormatter()))

    val serverEntry = Configuration.ServerEntry(parsedArgs.server, parsedArgs.port.toInt())
    val configuration = Configuration.Builder()
            .setName(parsedArgs.nick)
            .setLogin(parsedArgs.nick)
            .setRealName("TacoBot 1.0 beta")
            .addAutoJoinChannel(parsedArgs.channel)
            .addServer(serverEntry)
            .setAutoReconnectDelay(1337)
            .setSocketFactory(SSLSocketFactory.getDefault())
            .addListener(MovieListener())
            .addListener(PingListener())
            .buildConfiguration()

    val bot = PircBotX(configuration)
    logger.info("Starting bot with configuration: $configuration")
    bot.startBot()
}

class Args(parser: ArgParser) {
    val server by parser.storing("-s", "--server", help = "The (SSL) server hostname").default("localhost")
    val port by parser.storing("-p", "--port", help = "IRC server port number").default("9999")
    val channel by parser.storing("-c", "--channel", help = "The channel to connect to")
    val nick by parser.storing("-n", "--nick", help = "Bot nick").default("TacoBot")
}
