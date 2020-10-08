package me.bytebeats;

import com.intellij.ui.JBColor;

public interface UISettingProvider {
    boolean isInHiddenMode();

    boolean isRedRise();

    default JBColor getTextColor(Double offset) {
        if (isInHiddenMode() || offset == 0.0) {
            return JBColor.DARK_GRAY;
        } else if (isRedRise()) {
            if (offset > 0) {
                return JBColor.RED;
            } else {
                return JBColor.GREEN;
            }
        } else {
            if (offset > 0) {
                return JBColor.GREEN;
            } else {
                return JBColor.RED;
            }
        }
    }
}
