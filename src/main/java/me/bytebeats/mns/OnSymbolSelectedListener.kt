package me.bytebeats.mns

/**
 * @Author bytebeats
 * @Email <happychinapc></happychinapc>@gmail.com>
 * @Github https://github.com/bytebeats
 * @Created on 2020/9/15 18:10
 * @Version 1.0
 * @Description TO-DO
 */

@Deprecated(
    message = "Replaced with OnItemClick<T>",
    replaceWith = ReplaceWith(expression = "Replaced with OnItemClick<T>", imports = emptyArray()),
    level = DeprecationLevel.WARNING
)
interface OnSymbolSelectedListener {
    fun onSelected(symbol: String?)
}