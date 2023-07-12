//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.ccbluex.liquidbounce.launch.data.legacyui.clickgui.style.styles.aerolite;

import com.google.gson.annotations.Expose;

public class Position {
    @Expose
    public float x;
    @Expose
    public float y;
    @Expose
    public float width;
    @Expose
    public float height;

    public Position(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public static Position empty() {
        return new Position(-1.0F, -1.0F, 0.0F, 0.0F);
    }

    public boolean isHovered(int mouseX, int mouseY) {
        return (float)mouseX >= this.x && (float)mouseX <= this.x + this.width && (float)mouseY >= this.y && (float)mouseY <= this.y + this.height;
    }

    public boolean isHovered(int mouseX, int mouseY, int offsetX, int offsetY, int cWidth, int cHeight) {
        return (float)mouseX >= this.x + (float)offsetX && (float)mouseX <= this.x + (float)offsetX + (float)cWidth && (float)mouseY >= this.y + (float)offsetY && (float)mouseY <= this.y + (float)offsetY + (float)cHeight;
    }

    public float[] clicksOff(int mouseX, int mouseY) {
        return new float[]{(float)mouseX - this.x, (float)mouseY - this.y};
    }
}
