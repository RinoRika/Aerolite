package net.ccbluex.liquidbounce.features.module.utils;

import static java.awt.Color.HSBtoRGB;

public class WexSideUtils {
    public static int rainbow(float phase) {
        float speed = 3000;
        float hue = (System.currentTimeMillis() % (int) speed) + phase * 3000;
        if (hue > speed) {
            hue -= speed;
        }
        hue /= speed;
        if (hue > 0.5F) {
            hue = 0.5F - (hue - 0.5F);
        }
        hue += 0.5F;
        return HSBtoRGB(hue, 0.7f, 1);
    }
    public static double createAnimation(double value) {
        return Math.sqrt(1 - Math.pow(value - 1, 2));
    }
}
