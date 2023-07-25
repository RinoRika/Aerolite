package net.ccbluex.liquidbounce.launch.data.legacyui.clickgui.style.styles.newdropdown;



import net.ccbluex.liquidbounce.cn.Fonts.Module.fonts.api.FontManager;
import net.ccbluex.liquidbounce.cn.Fonts.Module.fonts.impl.SimpleFontManager;
import net.ccbluex.liquidbounce.launch.data.legacyui.clickgui.style.styles.newdropdown.SideGui.SideGui;
;


public class WbxMain {
    public static String Name = "Aerolite";
    public static String version = "";
    public static String username;
    private final SideGui sideGui = new SideGui();
    private static WbxMain INSTANCE;

    public  SideGui getSideGui() {
        return sideGui;
    }
    public static WbxMain getInstance() {
        try {
            if (INSTANCE == null) INSTANCE = new WbxMain();
            return INSTANCE;
        } catch (Throwable t) {
            //    ClientUtils.getLogger().warn(t);
            throw t;
        }
    }
    public static FontManager fontManager = SimpleFontManager.create();
    public static FontManager getFontManager() {
        return fontManager;
    }
}