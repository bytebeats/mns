package me.bytebeats.mns.meta

import java.util.*

data class DigitalCurrency(
    val symbol: String,
    val name: String,
    val preClose: String,
    val price: String,
    val volume: String,
) {
    private val pre: Double = preClose.toDouble()
    private val p: Double = price.toDouble()

    fun formattedPnl(): String {
        return String.format("%.4f", pnl())
    }

    fun getPnlR(): String {
        return String.format("%.2f", (p / pre - 1) * 100) + "%"
    }

    fun pnl(): Double = p - pre

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val digitalCurrency = other as DigitalCurrency
        return symbol == digitalCurrency.symbol
    }

    override fun hashCode(): Int {
        return Objects.hash(symbol)
    }
}
