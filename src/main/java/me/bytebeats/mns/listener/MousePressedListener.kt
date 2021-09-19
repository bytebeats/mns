package me.bytebeats.mns.listener

import java.awt.event.MouseEvent
import java.awt.event.MouseListener

/**
 * @Author bytebeats
 * @Email <happychinapc@gmail.com>
 * @Github https://github.com/bytebeats
 * @Created at 2021/9/19 15:30
 * @Version 1.0
 * @Description To listen to press event from mouse
 */

abstract class MousePressedListener : MouseListener {
    override fun mouseClicked(e: MouseEvent?) {}

    override fun mouseReleased(e: MouseEvent?) {}

    override fun mouseEntered(e: MouseEvent?) {}

    override fun mouseExited(e: MouseEvent?) {}
}