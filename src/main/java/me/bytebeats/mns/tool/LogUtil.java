package me.bytebeats.mns.tool;

import com.intellij.notification.*;
import com.intellij.openapi.project.ex.ProjectManagerEx;

/**
 * @Author bytebeats
 * @Email <happychinapc@gmail.com>
 * @Github https://github.com/bytebeats
 * @Created at 2021/9/19 15:06
 * @Version 1.0
 * @Description To display message on Event Log Window
 */

public class LogUtil {
    private static final NotificationGroup NOTIFICATION_GROUP = NotificationGroup.logOnlyGroup("Mns Event");

    public static void info(String message) {
        NOTIFICATION_GROUP.createNotification("Mns", message, NotificationType.INFORMATION, null).notify(ProjectManagerEx.getInstance().getDefaultProject());
    }
}
