package unit.auctionsniper

import auctionsniper.AuctionSniper
import auctionsniper.AuctionEventListener.PriceSource
import auctionsniper.SniperListener
import auctionsniper.Auction
import auctionsniper.AuctionEventListener
import org.jmock.junit5.JUnit5Mockery
import org.junit.jupiter.api.Test

class AuctionSniperTest {
    private val context = JUnit5Mockery()
    private val sniperListener = context.mock(SniperListener::class.java)
    private val auction = context.mock(Auction::class.java)
    private val sniper = AuctionSniper(auction, sniperListener)

    @Test
    fun `reports lost when auction closes`() {
        context.expect {
            oneOf(sniperListener).sniperLost()
        }.whenRunning {
            sniper.auctionClosed()
        }
    }

    @Test
    fun `bids higher and reports bidding when new price arrives`() {
        val price = 1001
        val increment = 25

        context.expect {
            oneOf(auction).bid(price + increment)
            atLeast(1).of(sniperListener).sniperBidding()
        }.whenRunning {
            sniper.currentPrice(price, increment, PriceSource.FromOtherBidder)
        }
    }

    @Test
    fun `report is winning when current price comes from sniper`() {
        context.expect {
            atLeast(1).of(sniperListener).sniperWinning()
        }.whenRunning {
            sniper.currentPrice(123, 45, PriceSource.FromSniper)
        }
    }
}