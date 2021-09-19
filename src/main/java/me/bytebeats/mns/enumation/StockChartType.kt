package me.bytebeats.mns.enumation

/**
 * @Author bytebeats
 * @Email <happychinapc@gmail.com>
 * @Github https://github.com/bytebeats
 * @Created at 2021/9/19 16:47
 * @Version 1.0
 * @Description 股票 K 线图类型
 */

enum class StockChartType(val type: String, val description: String) {
    Minute("min", "分时图"),
    Daily("daily", "日K图"),
    Weekly("weekly", "周K图"),
    Monthly("monthly", "月K图");
}