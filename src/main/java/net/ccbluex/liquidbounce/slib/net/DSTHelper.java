package net.ccbluex.liquidbounce.slib.net;


import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.slib.hhc.HDUtils;
import net.ccbluex.liquidbounce.slib.hhc.WebUtils;
import net.ccbluex.liquidbounce.slib.sys.SystemUtils;
import org.lwjgl.opengl.Display;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class DSTHelper {
    public static String Name = "AeroLite";
    public static void Liquid() {
        Display.setTitle(Name + " " + version + " | Login");
    }

    public static String Cracked = "";
    public static String version = LiquidBounce.CLIENT_REAL_VERSION;
    public static String username;
    public static boolean isStarting;

    public static void sendWindowsMessageLogin() throws AWTException, IOException {
        //  FakeSenseMain.Cracked();
        SystemUtils.displayTray("请输入用户名", "Login", TrayIcon.MessageType.WARNING);
        String AT = JOptionPane.showInputDialog("请输入用户名");
        final int R = 0;
        DSTHelper.username = AT;
        DSTHelper.isStarting = true;

        try {
            if (username == null) {
                JOptionPane.showMessageDialog(null, "Username cant be nothing!", "Check", 0);
                System.exit(0);
            } else {
                if (WebUtils.get("https://gitee.com/starslight/al-hwid/blob/master/main.txt")
                        .contains(HDUtils.getHWID())) {
                    JOptionPane.showMessageDialog(null, "Login Successfully", "Checker", 1);
                    SystemUtils.displayTray("Login Successfully", "Loading...", TrayIcon.MessageType.WARNING);
                } else {
                    JOptionPane.showMessageDialog(null, "Login Failed", "Checker", 0);
                    JOptionPane.showInputDialog(null, "Haven't Bind HWID", HDUtils.getHWID());
                    SystemUtils.displayTray(":( Use With IQ [!]", ":( Use With IQ [!]", TrayIcon.MessageType.WARNING);
                    System.exit(0);
                }
            }
        } catch (NoSuchAlgorithmException | IOException e) {
            JOptionPane.showMessageDialog(null, "UnKnow Host[!]");
            SystemUtils.displayTray("UnKnow Host[!]", "UnKnow Host[!]", TrayIcon.MessageType.WARNING);
            System.exit(0);
        }
    }
}