package me.bytebeats.mns.listener

/**
 * @Author bytebeats
 * @Email <happychinapc@gmail.com>
 * @Github https://github.com/bytebeats
 * @Created at 2021/9/19 15:07
 * @Version 1.0
 * @Description To listen to right-click event of mouse
 */

interface OnItemRightClickListener<T> {
    fun onItemRightClick(t: T, xOnScreen: Int, yOnScreen: Int)
}