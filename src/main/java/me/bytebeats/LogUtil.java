package me.bytebeats;

import com.intellij.notification.*;
import com.intellij.openapi.project.Project;

public class LogUtil {
    private static Project project;

    public static void init(Project project) {
        LogUtil.project = project;
    }

    public static void info(String message) {
        Notifications.Bus.notify(new Notification("mns_log", "Mns", message, NotificationType.INFORMATION), project);
    }
}
