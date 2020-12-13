package auctionsniper

import auctionsniper.AuctionEventListener.PriceSource
import org.jivesoftware.smack.Chat
import org.jivesoftware.smack.MessageListener
import org.jivesoftware.smack.packet.Message

class AuctionMessageTranslator(
    private val sniperId: String,
    private val listener: AuctionEventListener) : MessageListener {

    override fun processMessage(chat: Chat?, message: Message) {
        val event = AuctionEvent.from(message.body)
        val type = event.type()
        if ("CLOSE" == type) {
            listener.auctionClosed()
        } else if ("PRICE" == type) {
            listener.currentPrice(event.currentPrice(), event.increment(), event.isFrom(sniperId))
        }
    }

    private fun unpackEventFrom(message: Message): HashMap<String, String> {
        val event = HashMap<String, String>()
        for (element in message.body.split(";").dropLastWhile { it.isEmpty() }) {
            val pair = element.split(":").map { it.trim() }
            event[pair[0]] = pair[1]
        }

        return event
    }

    private class AuctionEvent {
        companion object {
            fun from(messageBody: String): AuctionEvent {
                val event = AuctionEvent()
                for (field in fieldsIn(messageBody)) {
                    event.addField(field)
                }

                return event
            }

            fun fieldsIn(messageBody: String): List<String> {
                return messageBody.split(";").dropLastWhile { it.isEmpty() }
            }
        }

        private val fields = HashMap<String, String>()

        fun type(): String {
            return get("Event")
        }

        fun currentPrice(): Int {
            return getInt("CurrentPrice")
        }

        fun isFrom(sniperId: String): PriceSource {
            return if (sniperId == bidder()) {
                PriceSource.FromSniper
            } else {
                PriceSource.FromOtherBidder
            }
        }

        private fun bidder(): String {
            return get("Bidder")
        }

        fun increment(): Int {
            return getInt("Increment")
        }

        private fun getInt(fieldName: String): Int {
            return get(fieldName).toInt()
        }

        private operator fun get(fieldName: String): String {
            return fields[fieldName]!!
        }

        private fun addField(field: String) {
            val pair = field.split(":").dropLastWhile { it.isEmpty() }.map { it.trim() }
            fields[pair[0]] = pair[1]
        }
    }
}
