package net.ccbluex.liquidbounce.slib.net;

import net.ccbluex.liquidbounce.slib.hhc.WebUtils;

import javax.swing.*;
import java.io.IOException;

public class SenseCore {

    public static void Cracked() throws IOException {
        final String version1 = "B8";
        if (DSTHelper.version.equals(version1)) {
        } else
            JOptionPane.showMessageDialog(null, "你的版本已经过时请更新");
        System.exit(0);
        if (version1 == null) {
        }else{
            if (WebUtils.get("https://gitee.com/starslight/al-hwid/blob/master/ver.txt").contains(version1)){
            }
        }
    }
}