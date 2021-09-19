package me.bytebeats.mns.tool;

import com.intellij.notification.*;
import com.intellij.openapi.project.ex.ProjectManagerEx;

/**
 * @Author bytebeats
 * @Email <happychinapc@gmail.com>
 * @Github https://github.com/bytebeats
 * @Created at 2021/9/19 15:06
 * @Version 1.0
 * @Description To display messages
 */

public class NotificationUtil {
    private static final NotificationGroup LOG_NOTIFICATION_GROUP = NotificationGroup.logOnlyGroup("Mns Log");
    private static final NotificationGroup BALLOON_NOTIFICATION_GROUP = NotificationGroup.balloonGroup("Mns Balloon");
    private static final NotificationGroup TOOL_WINDOW_NOTIFICATION_GROUP = NotificationGroup.toolWindowGroup("Mns Tool Window", "Mns Tool Window");

    /**
     * messages on Event Log Window
     *
     * @param message
     */
    public static void info(String message) {
        LOG_NOTIFICATION_GROUP.createNotification("Mns", message, NotificationType.INFORMATION, null).notify(ProjectManagerEx.getInstance().getDefaultProject());
    }

    /**
     * messages on Event Log Window in balloon style
     *
     * @param message
     */
    public static void infoBalloon(String message) {
        BALLOON_NOTIFICATION_GROUP.createNotification("Mns", message, NotificationType.WARNING, null).notify(ProjectManagerEx.getInstance().getDefaultProject());
    }

    /**
     * messages on Tool Window
     *
     * @param message
     */
    public static void infoToolWindow(String message) {
        TOOL_WINDOW_NOTIFICATION_GROUP.createNotification("Mns", message, NotificationType.ERROR, null).notify(ProjectManagerEx.getInstance().getDefaultProject());
    }
}
