package net.ccbluex.liquidbounce.cn.Fonts.newdropdown.utils.normal;

import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleManager;

import java.util.List;
import java.util.stream.Collectors;

public class Main{

    public static int categoryCount;

    public static boolean reloadModules = true;

    public static float allowedClickGuiHeight = 300;

    /**
     * 一个个添加
     */

    public static List<Module> getModulesInCategory(ModuleCategory c, ModuleManager moduleManager) {
        return moduleManager.getModules().stream().filter(m -> m.getCategory() == c).collect(Collectors.toList());
    }

    public static float getAllowedClickGUIHeight() {
        return allowedClickGuiHeight;
    }
}