package net.ccbluex.liquidbounce.utils;

import net.ccbluex.liquidbounce.LiquidBounce;
import org.lwjgl.opengl.Display;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/* loaded from: LiquidBounce-b73.jar:Core/Insane/WbxMain.class */
public class WbxMain {
    public static String username;
    public static String password;
    public static boolean isStarting;
    public static String Name = LiquidBounce.CLIENT_NAME;
    public static String Rank = "";
    public static String version = "Build 220123";
    public static boolean onDebug = false;

    public static void Main() {
        Display.setTitle(Name + " Loading.....");
    }

    public static void Liquid() {
        Display.setTitle(Name + " " + version);
    }

    public static void sendWindowsMessageLogin() throws AWTException, IOException {
        username = JOptionPane.showInputDialog("Please enter your QQ number");
        isStarting = true;
        if (username == null) {
            JOptionPane.showMessageDialog((Component) null, "QQ\u53f7\u4e0d\u80fd\u4e3a\u7a7a!", "Logoget", 0);
            System.exit(0);
        }
    }
}
