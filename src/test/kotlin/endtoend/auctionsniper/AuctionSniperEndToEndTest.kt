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
        auction.hasReceivedJoinRequestFromSniper()
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