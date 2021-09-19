package me.bytebeats.mns.listener

/**
 * @Author bytebeats
 * @Email <happychinapc@gmail.com>
 * @Github https://github.com/bytebeats
 * @Created at 2021/9/19 15:06
 * @Version 1.0
 * @Description To listen to left-double-click event of mouse
 */

interface OnItemDoubleClick<T> {
    fun onItemDoubleClick(t: T, xOnScreen: Int, yOnScreen: Int)
}