package me.bytebeats;

import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;

public class LogUtil {
    private static Project project;

    public static void init(Project project) {
        LogUtil.project = project;
    }

    public static void info(String message) {
        new NotificationGroup("mns_log", NotificationDisplayType.NONE, true).createNotification(message, MessageType.INFO).notify(project);
    }
}
