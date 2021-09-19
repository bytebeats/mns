package me.bytebeats.mns.ui

import com.intellij.openapi.project.ex.ProjectManagerEx
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.ui.awt.RelativePoint
import com.intellij.ui.tabs.TabInfo
import com.intellij.ui.tabs.impl.JBTabsImpl
import me.bytebeats.mns.enumation.FundChartType
import me.bytebeats.mns.tool.NotificationUtil
import me.bytebeats.mns.tool.StringResUtils
import java.awt.Point
import java.net.MalformedURLException
import java.net.URL
import javax.swing.ImageIcon
import javax.swing.JLabel

object PopupsUtil {
    fun popFundChart(code: String, chartType: FundChartType, anchor: Point) {
        if (ProjectManagerEx.getInstance().defaultProject.isDisposed) {
            return
        }
        if (chartType != FundChartType.EstimatedNetWorth) {
            NotificationUtil.infoBalloon("To be implemented!")
            return
        }
        val netWorthTabInfo = try {
            TabInfo(
                JLabel(
                    ImageIcon(
                        URL(
                            StringResUtils.URL_FUND_CHART_ESTIMATED_NET_WORTH.format(
                                code,
                                System.currentTimeMillis()
                            )
                        )
                    )
                )
            )
        } catch (ignore: MalformedURLException) {
            NotificationUtil.info(ignore.message)
            return
        }
        netWorthTabInfo.text = chartType.description
        val tabs = JBTabsImpl(ProjectManagerEx.getInstance().defaultProject)
        tabs.addTab(netWorthTabInfo)
        JBPopupFactory.getInstance()
            .createComponentPopupBuilder(tabs, null)
            .setMovable(true)
            .setRequestFocus(true)
            .createPopup()
            .show(RelativePoint.fromScreen(anchor))
    }
}