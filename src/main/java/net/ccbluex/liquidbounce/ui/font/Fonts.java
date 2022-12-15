package net.ccbluex.liquidbounce.ui.font;

import com.google.gson.*;
import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.utils.ClientUtils;
import net.ccbluex.liquidbounce.utils.FileUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import java.awt.*;
import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Fonts {

    @FontDetails(fontName = "Small", fontSize = 19, fileName = "regular.ttf")
    public static GameFontRenderer font35;

    @FontDetails(fontName = "Medium", fontSize = 20, fileName = "regular.ttf")
    public static GameFontRenderer font40;

    @FontDetails(fontName = "Medium2", fontSize = 35, fileName = "regular.ttf")
    public static GameFontRenderer font70;

    @FontDetails(fontName = "Medium3", fontSize = 40, fileName = "regular.ttf")
    public static GameFontRenderer font100;

    @FontDetails(fontName = "Medium4", fontSize = 36, fileName = "regular.ttf")
    public static GameFontRenderer font72;

    @FontDetails(fontName = "abcdwwqqd", fontSize = 16, fileName = "regular.ttf")
    public static GameFontRenderer font32;

    @FontDetails(fontName = "Medium5", fontSize = 26, fileName = "regular.ttf")
    public static GameFontRenderer font52;

    @FontDetails(fontName = "abcdwwqqd2352", fontSize = 15, fileName = "regular.ttf")
    public static GameFontRenderer font30;

    @FontDetails(fontName = "abcdwwqqd1342", fontSize = 15, fileName = "regular.ttf")
    public static GameFontRenderer font24;
    @FontDetails(fontName = "abcdwwqqds", fontSize = 19, fileName = "PS.ttf")
    public static GameFontRenderer font37;

    @FontDetails(fontName = "wd1qqds", fontSize = 28, fileName = "mainmenu.ttf")
    public static GameFontRenderer mainmenu;

    @FontDetails(fontName = "iconsnovo", fontSize = 40, fileName = "novoline.ttf")
    public static GameFontRenderer Nicon80;

    @FontDetails(fontName = "GoogleSans", fontSize = 18, fileName = "GoogleSans.ttf")
    public static GameFontRenderer gs35;


    @FontDetails(fontName = "Jello", fontSize = 18, fileName = "Jello.ttf")
    public static GameFontRenderer fontJello;

    @FontDetails(fontName = "ABCD1234", fontSize = 80, fileName = "GoogleSans.ttf")
    public static GameFontRenderer mainMenu2;
    @FontDetails(fontName = "GoogleSans", fontSize = 18, fileName = "GoogleSans.ttf")
    public static GameFontRenderer gs15;

    @FontDetails(fontName = "GoogleSans", fontSize = 20, fileName = "GoogleSans.ttf")
    public static GameFontRenderer gs40;

    @FontDetails(fontName = "GoogleSans", fontSize = 30, fileName = "GoogleSans.ttf")
    public static GameFontRenderer gs60;

    @FontDetails(fontName = "PR", fontSize = 18, fileName = "PR.ttf")
    public static GameFontRenderer poppinsBold20;

    @FontDetails(fontName = "PS", fontSize = 18, fileName = "PS.ttf")
    public static GameFontRenderer poppins16;

    @FontDetails(fontName = "Tenacity", fontSize = 18, fileName = "Tenacity.ttf")
    public static GameFontRenderer tc35;

    @FontDetails(fontName = "Tenacity", fontSize = 20, fileName = "Tenacity.ttf")
    public static GameFontRenderer tc40;

    @FontDetails(fontName = "Tenacity", fontSize = 23, fileName = "Tenacity.ttf")
    public static GameFontRenderer tc45;

    @FontDetails(fontName = "Tenacity", fontSize = 25, fileName = "Tenacity.ttf")
    public static GameFontRenderer tc50;

    @FontDetails(fontName = "Tenacity", fontSize = 50, fileName = "Tenacity.ttf")
    public static GameFontRenderer tc100;

    @FontDetails(fontName = "Icons", fontSize = 20, fileName = "Icon.ttf")
    public static GameFontRenderer icon30;

    @FontDetails(fontName = "Icons", fontSize = 23, fileName = "Icon.ttf")
    public static GameFontRenderer icon35;

    @FontDetails(fontName = "Icons", fontSize = 25, fileName = "Icon.ttf")
    public static GameFontRenderer icon40;

    @FontDetails(fontName = "Bangers", fontSize = 23, fileName = "Bangers.ttf")
    public static GameFontRenderer fontBangers;

    @FontDetails(fontName = "Neverlose18", fontSize = 18, fileName = "neverlose500.ttf")
    public static GameFontRenderer NL18;

    @FontDetails(fontName = "Neverlose16", fontSize = 16, fileName = "neverlose500.ttf")
    public static GameFontRenderer NL16;

    @FontDetails(fontName = "Neverlose20", fontSize = 20, fileName = "neverlose500.ttf")
    public static GameFontRenderer NL20;

    @FontDetails(fontName = "Neverlose24", fontSize = 24, fileName = "neverlose500.ttf")
    public static GameFontRenderer NL24;

    @FontDetails(fontName = "Neverlose35", fontSize = 35, fileName = "neverlose500.ttf")
    public static GameFontRenderer NL35;

    @FontDetails(fontName = "Minecraft Font")
    public static final FontRenderer minecraftFont = Minecraft.getMinecraft().fontRendererObj;

    private static final List<GameFontRenderer> CUSTOM_FONT_RENDERERS = new ArrayList<>();

    public static void loadFonts() {
        long l = System.currentTimeMillis();

        ClientUtils.INSTANCE.logInfo("Loading Fonts.");

        for(GameFontRenderer it : getCustomFonts()) {
            it.close();
        }

        initFonts();

        for(final Field field : Fonts.class.getDeclaredFields()) {
            try {
                field.setAccessible(true);
                final FontDetails fontDetails = field.getAnnotation(FontDetails.class);

                if(fontDetails!=null) {
                    if(!fontDetails.fileName().isEmpty())
                        field.set(null,new GameFontRenderer(getFont(fontDetails.fileName(), fontDetails.fontSize())));
                }
            }catch(final IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        try {
            CUSTOM_FONT_RENDERERS.clear();

            final File fontsFile = new File(LiquidBounce.fileManager.getFontsDir(), "fonts.json");

            if(fontsFile.exists()) {
                final JsonElement jsonElement = new JsonParser().parse(new BufferedReader(new FileReader(fontsFile)));

                if(jsonElement instanceof JsonNull)
                    return;

                final JsonArray jsonArray = (JsonArray) jsonElement;

                for(final JsonElement element : jsonArray) {
                    if(element instanceof JsonNull)
                        return;

                    final JsonObject fontObject = (JsonObject) element;

                    CUSTOM_FONT_RENDERERS.add(new GameFontRenderer(getFont(fontObject.get("fontFile").getAsString(), fontObject.get("fontSize").getAsInt())));
                }
            }else{
                fontsFile.createNewFile();

                final PrintWriter printWriter = new PrintWriter(new FileWriter(fontsFile));
                printWriter.println(new GsonBuilder().setPrettyPrinting().create().toJson(new JsonArray()));
                printWriter.close();
            }
        }catch(final Exception e) {
            e.printStackTrace();
        }

        ClientUtils.INSTANCE.logInfo("Loaded Fonts. (" + (System.currentTimeMillis() - l) + "ms)");
    }

    private static void initFonts() {
        try {
            initSingleFont("regular.ttf","assets/minecraft/Fonts/regular.ttf");
            initSingleFont("SessionInfo.ttf","assets/minecraft/Fonts/SessionInfo.ttf");
            initSingleFont("Icon.ttf","assets/minecraft/Fonts/Icon.ttf");
            initSingleFont("PS.ttf","assets/minecraft/Fonts/PS.ttf");
            initSingleFont("PR.ttf","assets/minecraft/Fonts/PR.ttf");
            initSingleFont("comfortaa.ttf","assets/minecraft/Fonts/comfortaa.ttf");
            initSingleFont("GoogleSans.ttf","assets/minecraft/Fonts/GoogleSans.ttf");
            initSingleFont("Hanabi.ttf","assets/minecraft/Fonts/Hanabi.ttf");
            initSingleFont("Tenacity.ttf","assets/minecraft/Fonts/Tenacity.ttf");
            initSingleFont("Bangers.ttf","assets/minecraft/Fonts/Bangers.ttf");
            initSingleFont("novoline.ttf","assets/minecraft/Fonts/novoline.ttf");
            initSingleFont("mainmenu.ttf","assets/minecraft/Fonts/mainmenu.ttf");
            initSingleFont("Jello.ttf", "assets/minecraft/Fonts/Jello.ttf");
            initSingleFont("neverlose500.ttf", "assets/minecraft/Fonts/neverlose500.ttf");
        }catch(IOException e) {
            e.printStackTrace();
        }
    }


    private static void initSingleFont(String name, String resourcePath) throws IOException {
        File file=new File(LiquidBounce.fileManager.getFontsDir(), name);
        if(!file.exists())
            FileUtils.INSTANCE.unpackFile(file, resourcePath);
    }

    public static FontRenderer getFontRenderer(final String name, final int size) {
        if(name.equals("Minecraft")){
            return minecraftFont;
        }

        for (final FontRenderer fontRenderer : getFonts()) {
            if(fontRenderer instanceof GameFontRenderer){
                GameFontRenderer liquidFontRenderer=(GameFontRenderer) fontRenderer;
                final Font font = liquidFontRenderer.getDefaultFont().getFont();

                if(font.getName().equals(name) && font.getSize() == size)
                    return liquidFontRenderer;
            }
        }

        return minecraftFont;
    }

    public static Object[] getFontDetails(final FontRenderer fontRenderer) {
        if (fontRenderer instanceof GameFontRenderer) {
            final Font font = ((GameFontRenderer) fontRenderer).getDefaultFont().getFont();

            return new Object[] {font.getName(), font.getSize()};
        }

        return new Object[] {"Minecraft", -1};
    }

    public static List<FontRenderer> getFonts() {
        final List<FontRenderer> fonts = new ArrayList<>();

        for(final Field fontField : Fonts.class.getDeclaredFields()) {
            try {
                fontField.setAccessible(true);

                final Object fontObj = fontField.get(null);

                if(fontObj instanceof FontRenderer) fonts.add((FontRenderer) fontObj);
            }catch(final IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        fonts.addAll(Fonts.CUSTOM_FONT_RENDERERS);

        return fonts;
    }

    public static List<GameFontRenderer> getCustomFonts() {
        final List<GameFontRenderer> fonts = new ArrayList<>();

        for(final Field fontField : Fonts.class.getDeclaredFields()) {
            try {
                fontField.setAccessible(true);

                final Object fontObj = fontField.get(null);

                if(fontObj instanceof GameFontRenderer) fonts.add((GameFontRenderer) fontObj);
            }catch(final IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        fonts.addAll(Fonts.CUSTOM_FONT_RENDERERS);

        return fonts;
    }

    private static Font getFont(final String fontName, final int size) {
        try {
            final InputStream inputStream = new FileInputStream(new File(LiquidBounce.fileManager.getFontsDir(), fontName));
            Font awtClientFont = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            awtClientFont = awtClientFont.deriveFont(Font.PLAIN, size);
            inputStream.close();
            return awtClientFont;
        }catch(final Exception e) {
            e.printStackTrace();

            return new Font("default", Font.PLAIN, size);
        }
    }
}