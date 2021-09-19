package me.bytebeats.mns.tool

import com.intellij.openapi.project.ex.ProjectManagerEx
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.ui.JBColor
import com.intellij.ui.awt.RelativePoint
import com.intellij.ui.tabs.TabInfo
import com.intellij.ui.tabs.TabsListener
import com.intellij.ui.tabs.impl.JBTabsImpl
import me.bytebeats.mns.enumation.FundChartType
import me.bytebeats.mns.enumation.StockChartType
import java.awt.Color
import java.awt.Point
import java.net.MalformedURLException
import java.net.URL
import javax.swing.ImageIcon
import javax.swing.JLabel

object PopupsUtil {
    /**
     * pop up k-line chart of funds
     */
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

    /**
     * stock chart url
     */
    private fun stockChartUrl(stockSymbol: String, chartType: StockChartType): String {
        val market = stockSymbol.take(2)
        val symbol = stockSymbol.substring(2)
        return when (market) {
            "sh", "sz" -> {
                StringResUtils.URL_SINA_CHART_CN_FORMATTER.format(
                    StringResUtils.URL_SINA_CHART,
                    chartType.type,
                    stockSymbol,
                    System.currentTimeMillis()
                )
            }
            "us" -> {
                if (chartType == StockChartType.Minute) {
                    StringResUtils.URL_SINA_CHART_MIN_FORMATTER.format(
                        StringResUtils.URL_SINA_CHART,
                        chartType.type,
                        market,
                        symbol,
                        System.currentTimeMillis()
                    )
                } else {
                    StringResUtils.URL_SINA_CHART_US_FORMATTER.format(
                        StringResUtils.URL_SINA_CHART,
                        market,
                        chartType.type,
                        symbol,
                        System.currentTimeMillis()
                    )
                }
            }
            "hk" -> {
                if (chartType == StockChartType.Minute) {
                    StringResUtils.URL_SINA_CHART_MIN_FORMATTER.format(
                        StringResUtils.URL_SINA_CHART,
                        chartType.type,
                        market,
                        symbol,
                        System.currentTimeMillis()
                    )
                } else {
                    StringResUtils.URL_SINA_CHART_HK_FORMATTER.format(
                        StringResUtils.URL_SINA_CHART,
                        market,
                        chartType.type,
                        symbol,
                        System.currentTimeMillis()
                    )
                }
            }
            else -> ""
        }
    }

    /**
     * pop up k-line charts of stock
     */
    fun popupStockChart(stockSymbol: String, chartType: StockChartType, anchor: Point) {
        if (ProjectManagerEx.getInstance().defaultProject.isDisposed) {
            return
        }
        val tabs = JBTabsImpl(ProjectManagerEx.getInstance().defaultProject)
        for (type in StockChartType.values()) {
            val chartUrl = stockChartUrl(stockSymbol, type)
            val label = JLabel()
            label.text = chartUrl
            val tabInfo = TabInfo(label)
            tabInfo.text = type.description
            tabs.addTab(tabInfo)
            if (type == chartType) {
                tabs.select(tabInfo, true)
                label.icon = try {
                    ImageIcon(URL(chartUrl))
                } catch (ignore: MalformedURLException) {
                    NotificationUtil.infoBalloon(ignore.message)
                    continue
                }
                NotificationUtil.info(chartUrl)
                label.text = null
            }
        }
        tabs.addListener(object : TabsListener {
            override fun selectionChanged(oldSelection: TabInfo?, newSelection: TabInfo?) {
                super.selectionChanged(oldSelection, newSelection)
                if (newSelection?.component is JLabel) {
                    val label = newSelection.component as JLabel
                    if (!label.text.isNullOrBlank()) {
                        NotificationUtil.info(label.text)
                        label.icon = try {
                            ImageIcon(URL(label.text))
                        } catch (ignore: MalformedURLException) {
                            NotificationUtil.infoBalloon(ignore.message)
                            return
                        }
                        label.text = null
                    }
                }
            }
        })
        JBPopupFactory.getInstance()
            .createComponentPopupBuilder(tabs, null)
            .setMovable(true)
            .setRequestFocus(true)
            .createPopup()
            .show(RelativePoint.fromScreen(anchor))
    }

}