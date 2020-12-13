package endtoend.auctionsniper

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AuctionSniperEndToEndTest {
    private lateinit var auction: FakeAuctionServer
    private lateinit var application: ApplicationRunner

//    @BeforeEach
    fun setupAuctionAndApplication() {
        auction = FakeAuctionServer("item-54321")
        application = ApplicationRunner()
    }

//    @Test
    fun `sniper wins an auction by bidding higher`() {
        auction.startSellingItem()
        application.startBiddingIn(auction)

        auction.hasReceivedJoinRequestFromSniper(ApplicationRunner.SNIPER_XMPP_ID)

        auction.reportPrice(1000, 98, "other bidder")
        application.hasShownSniperIsBidding()

        auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID)

        auction.reportPrice(1098, 97, ApplicationRunner.SNIPER_XMPP_ID)
        application.hasShownSniperisWinning()

        auction.announceClosed()
        application.showsSniperHasWonAuction()
    }

//    @Test
    fun `sniper joins auction until auction closes`() {
        auction.startSellingItem()
        application.startBiddingIn(auction)
        auction.hasReceivedJoinRequestFromSniper(ApplicationRunner.SNIPER_XMPP_ID)

        auction.reportPrice(1000, 98, "other bidder")
        application.hasShownSniperIsBidding()

        auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID)

        auction.announceClosed()
        application.showsSniperHasLostAuction()
    }

//    @AfterEach
    fun `stop auction`() {
        auction.stop()
    }

//    @AfterEach
    fun `stop application`() {
        application.stop()
    }
}