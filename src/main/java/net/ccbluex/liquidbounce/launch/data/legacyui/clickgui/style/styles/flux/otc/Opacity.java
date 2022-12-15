/*
 * Decompiled By ProdiGai At QQ1719993693 With Tool  0.150.
 */
package net.ccbluex.liquidbounce.launch.data.legacyui.clickgui.style.styles.flux.otc;


public class Opacity {
    private float opacity;
    private long lastMS;

    public Opacity(int opacity) {
        this.opacity = opacity;
        this.lastMS = System.currentTimeMillis();
    }

    public void interpolate(float targetOpacity) {
        long currentMS = System.currentTimeMillis();
        long delta = currentMS - this.lastMS;
        this.lastMS = currentMS;
        this.opacity = AnimationUtil.calculateCompensation(targetOpacity, this.opacity, delta, 20);
    }

    public void interp(float targetOpacity, int speed) {
        long currentMS = System.currentTimeMillis();
        long delta = currentMS - this.lastMS;
        this.lastMS = currentMS;
        this.opacity = AnimationUtil.calculateCompensation(targetOpacity, this.opacity, delta, speed);
    }

    public float getOpacity() {
        return (int)this.opacity;
    }

    public void setOpacity(float opacity) {
        this.opacity = opacity;
    }
}

