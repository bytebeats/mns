package me.bytebeats.mns.listener

import java.awt.event.WindowEvent
import java.awt.event.WindowListener

/**
 * @Author bytebeats
 * @Email <happychinapc@gmail.com>
 * @Github https://github.com/bytebeats
 * @Created at 2021/9/19 15:58
 * @Version 1.0
 * @Description To detect whether window is opened or closed
 */

abstract class WindowSwitchListener : WindowListener {
    override fun windowClosing(e: WindowEvent?) {}

    override fun windowIconified(e: WindowEvent?) {}

    override fun windowDeiconified(e: WindowEvent?) {}

    override fun windowActivated(e: WindowEvent?) {}

    override fun windowDeactivated(e: WindowEvent?) {}
}