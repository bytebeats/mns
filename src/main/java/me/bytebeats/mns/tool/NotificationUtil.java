package me.bytebeats.mns.tool;

import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
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
    private static final String NOTIFICATION_TITLE = "Money Never Sleeps";
    private static final String LOG_NOTIFICATION_GROUP = "Mns Log";
    private static final String BALLOON_NOTIFICATION_GROUP = "Mns Balloon";
    private static final String TOOL_WINDOW_NOTIFICATION_GROUP = "Mns Tool Window";

    /**
     * messages on Event Log Window
     *
     * @param message
     */
    public static void info(String message) {
        NotificationGroupManager.getInstance()
                .getNotificationGroup(LOG_NOTIFICATION_GROUP)
                .createNotification(NOTIFICATION_TITLE, message, NotificationType.INFORMATION)
                .notify(ProjectManagerEx.getInstance().getDefaultProject());
    }

    /**
     * messages on Event Log Window in balloon style
     *
     * @param message
     */
    public static void infoBalloon(String message) {
        NotificationGroupManager.getInstance()
                .getNotificationGroup(BALLOON_NOTIFICATION_GROUP)
                .createNotification(NOTIFICATION_TITLE, message, NotificationType.WARNING)
                .notify(ProjectManagerEx.getInstance().getDefaultProject());
    }

    /**
     * messages on Tool Window
     *
     * @param message
     */
    public static void infoToolWindow(String message) {
        NotificationGroupManager.getInstance()
                .getNotificationGroup(TOOL_WINDOW_NOTIFICATION_GROUP)
                .createNotification(NOTIFICATION_TITLE, message, NotificationType.ERROR)
                .notify(ProjectManagerEx.getInstance().getDefaultProject());
    }
}
