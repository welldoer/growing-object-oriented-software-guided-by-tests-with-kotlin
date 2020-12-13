package endtoend.auctionsniper

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

class AuctionSniperEndToEndTest {
    companion object {
        val auction = FakeAuctionServer("item-54321")
        val application = ApplicationRunner()
    }

    @Test
    fun `sniper joins auction until auction closes`() {
        auction.StartSellingItem()
        application.startBiddingIn(auction)
        auction.hasReceivedJoinRequestFromSniper(ApplicationRunner.SNIPER_XMPP_ID)

        auction.reportPrice(1000, 98, "other bidder")
        application.hasShownSniperIsBidding()

        auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID)

        auction.announceClosed()
        application.showsSniperHasLostAuction()
    }

    @AfterEach
    fun `stop auction`() {
        auction.stop()
    }

    @AfterEach
    fun `stop application`() {
        application.stop()
    }
}