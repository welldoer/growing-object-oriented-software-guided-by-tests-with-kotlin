package endtoend.auctionsniper

import auctionsniper.MainWindow
import com.objogate.wl.swing.AWTEventQueueProber
import com.objogate.wl.swing.driver.ComponentDriver
import com.objogate.wl.swing.driver.JFrameDriver
import com.objogate.wl.swing.driver.JLabelDriver
import com.objogate.wl.swing.gesture.GesturePerformer
import org.hamcrest.CoreMatchers

class AuctionSniperDriver(timeoutMillis: Long): JFrameDriver (
    GesturePerformer(),
    topLevelFrame(named(MainWindow.MAIN_WINDOW_NAME), ComponentDriver.showingOnScreen()),
    AWTEventQueueProber(timeoutMillis, 100)
) {
    fun showsSniperStatus(statusText: String) {
        JLabelDriver(this, named(MainWindow.SNIPER_STATUS_NAME)).hasText(CoreMatchers.equalTo(statusText))
    }
}
