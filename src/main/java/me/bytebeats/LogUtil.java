package me.bytebeats;

import com.intellij.notification.*;
import com.intellij.openapi.project.Project;

public class LogUtil {
    private static Project project;
    private static final NotificationGroup NOTIFICATION_GROUP = NotificationGroup.logOnlyGroup("Mns Event");

    public static void init(Project project) {
        LogUtil.project = project;
    }

    public static void info(String message) {
        NOTIFICATION_GROUP.createNotification("Mns", message, NotificationType.INFORMATION, null).notify(project);
    }
}
