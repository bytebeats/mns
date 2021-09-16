package me.bytebeats.mns.tool;

import java.math.RoundingMode;
import java.text.NumberFormat;

public class NumberFormatUtils {
    private final static NumberFormat NUMBER_FORMAT = NumberFormat.getNumberInstance();

    static {
        NUMBER_FORMAT.setMaximumFractionDigits(3);
        NUMBER_FORMAT.setRoundingMode(RoundingMode.HALF_UP);
    }

    public static String formatDouble(double value) {
        return NUMBER_FORMAT.format(value);
    }
}
