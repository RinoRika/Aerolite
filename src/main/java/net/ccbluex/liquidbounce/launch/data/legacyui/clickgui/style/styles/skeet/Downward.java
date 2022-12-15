//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.ccbluex.liquidbounce.launch.data.legacyui.clickgui.style.styles.skeet;

import net.ccbluex.liquidbounce.value.Value;
import net.minecraft.client.gui.Gui;


public abstract class Downward<V extends Value> extends Gui {
    public V setting;
    private float x;
    private float y;
    private int width;
    private int height;
    public Position pos;
    public ModuleRender moduleRender;

    public Downward(V s, float x, float y, int width, int height, ModuleRender moduleRender) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.setting = s;
        this.pos = new Position(this.getX(), this.getY(), (float)this.getWidth(), (float)this.getHeight());
        this.moduleRender = moduleRender;
    }

    public abstract void draw(int var1, int var2);

    public abstract void mouseClicked(int var1, int var2, int var3);

    public void keyTyped(char typedChar, int keyCode) {
    }

    public abstract void mouseReleased(int var1, int var2, int var3);

    public void update() {
        this.pos = new Position(this.getX(), this.getY(), (float)this.getWidth(), (float)this.getHeight());
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public int getHeight() {
        return this.height;
    }

    public int getWidth() {
        return this.width;
    }

    public void setX(int x) {
        this.x = (float)x;
    }

    public void setY(int y) {
        this.y = (float)y;
    }

    public int getScrollY() {
        return this.moduleRender.getScrollY();
    }
}
