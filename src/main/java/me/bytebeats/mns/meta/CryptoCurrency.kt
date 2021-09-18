package me.bytebeats.mns.meta

import java.util.*

data class CryptoCurrency(
    val symbol: String,
    val name: String,
    val preClose: String,
    val price: String,
    val volume: String,
) {
    private val pre: Double = preClose.toDouble()
    private val p: Double = price.toDouble()

    fun getPnl(): String {
        return String.format("%.4f", p - pre)
    }

    fun getPnlR(): String {
        return String.format("%.2f\\%", (p / pre - 1) * 100)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val cryptoCurrency = other as CryptoCurrency
        return symbol == cryptoCurrency.symbol
    }

    override fun hashCode(): Int {
        return Objects.hash(symbol)
    }
}
