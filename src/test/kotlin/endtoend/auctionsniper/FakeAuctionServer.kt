package endtoend.auctionsniper

import auctionsniper.SOLProtocol
import org.hamcrest.CoreMatchers
import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.jivesoftware.smack.Chat
import org.jivesoftware.smack.MessageListener
import org.jivesoftware.smack.XMPPConnection
import org.jivesoftware.smack.packet.Message
import java.lang.String.format
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.TimeUnit

class FakeAuctionServer(internal val itemId: String) {
    companion object {
        const val ITEM_ID_AS_LOGIN = "auction-%s"
        const val AUCTION_RESOURCE = "Auction"
        const val XMPP_HOSTNAME = "localhost"
        const val AUCTION_PASSWORD = "auction"
    }

    private val connection: XMPPConnection
    private val messageListener: SingleMessageListener = SingleMessageListener()
    private var currentChat: Chat? = null

    init {
        connection = XMPPConnection(XMPP_HOSTNAME)
    }

    fun startSellingItem() {
        connection.connect()

        connection.login(format(ITEM_ID_AS_LOGIN, itemId), AUCTION_PASSWORD, AUCTION_RESOURCE)
        connection.chatManager.addChatListener { chat: Chat, _:Boolean ->
            currentChat = chat
            chat.addMessageListener(messageListener)
        }
    }

    fun hasReceivedJoinRequestFromSniper(sniperId: String) {
        receivesAMessageMatching(sniperId, Matchers.equalTo(SOLProtocol.joinCommand()))
    }

    fun announceClosed() {
        currentChat?.sendMessage(SOLProtocol.closeEvent())
    }

    fun stop() {
        connection.disconnect()
    }

    fun reportPrice(price: Int, increment: Int, bidder: String) {
        currentChat?.sendMessage(SOLProtocol.priceEvent(price, increment, bidder))
    }

    fun hasReceivedBid(bid: Int, sniperId: String) {
        receivesAMessageMatching(sniperId, Matchers.equalTo(SOLProtocol.bidCommand(bid)))
    }

    private fun receivesAMessageMatching(sniperId: String, matcher: Matcher<String>) {
        messageListener.receivesAMessage(matcher)
        MatcherAssert.assertThat(currentChat?.participant, Matchers.equalTo(sniperId))
    }
}

class SingleMessageListener(
    private val messages: ArrayBlockingQueue<Message> = ArrayBlockingQueue(1)
): MessageListener {
    override fun processMessage(chat: Chat?, message: Message?) {
        messages.add(message)
    }

    fun receivesAMessage(matcher: Matcher<String>) {
        val message = messages.poll(10, TimeUnit.SECONDS)

        MatcherAssert.assertThat("Message", message, CoreMatchers.notNullValue())
        MatcherAssert.assertThat(message.body, matcher)
    }
}
