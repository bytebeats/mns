package me.bytebeats.mns.meta

import java.util.*

data class CryptoCurrency(
    val symbol: String,
    val name: String,
    val price: String,
    val volume: String,
    val pnlr: String
) {
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
