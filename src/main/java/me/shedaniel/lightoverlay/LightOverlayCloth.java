package me.shedaniel.lightoverlay;

import me.shedaniel.forge.clothconfig2.api.ConfigBuilder;
import me.shedaniel.forge.clothconfig2.api.ConfigCategory;
import me.shedaniel.forge.clothconfig2.impl.ConfigEntryBuilderImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;

import java.util.Locale;
import java.util.Optional;
import java.util.function.BiFunction;

public class LightOverlayCloth {
    public static void register() {
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> (BiFunction<Minecraft, Screen, Screen>) (client, parent) -> {
            ConfigBuilder builder = ConfigBuilder.create().setParentScreen(parent).setTitle("key.lightoverlay.category");
            
            ConfigEntryBuilderImpl eb = builder.getEntryBuilder();
            ConfigCategory general = builder.getOrCreateCategory("config.lightoverlay-forge.general");
            general.addEntry(eb.startIntSlider("config.lightoverlay-forge.reach", LightOverlayClient.reach, 1, 50).setDefaultValue(7).setTextGetter(integer -> "Reach: " + integer + " Blocks").setSaveConsumer(integer -> LightOverlayClient.reach = integer).build());
            general.addEntry(eb.startIntSlider("config.lightoverlay-forge.lineWidth", MathHelper.floor(LightOverlayClient.lineWidth * 100), 100, 700).setDefaultValue(100).setTextGetter(integer -> "Light Width: " + LightOverlayClient.FORMAT.format(integer / 100d)).setSaveConsumer(integer -> LightOverlayClient.lineWidth = integer / 100f).build());
            general.addEntry(eb.startStrField("config.lightoverlay-forge.yellowColor", "#" + toStringColor(LightOverlayClient.yellowColor)).setDefaultValue("#FFFF00").setSaveConsumer(str -> LightOverlayClient.yellowColor = toIntColor(str)).setErrorSupplier(s -> {
                if (!s.startsWith("#") || s.length() != 7 || !isInt(s.substring(1)))
                    return Optional.of(I18n.format("config.lightoverlay-forge.invalidColor"));
                else
                    return Optional.empty();
            }).build());
            general.addEntry(eb.startStrField("config.lightoverlay-forge.redColor", "#" + toStringColor(LightOverlayClient.redColor)).setDefaultValue("#FF0000").setSaveConsumer(str -> LightOverlayClient.redColor = toIntColor(str)).setErrorSupplier(s -> {
                if (!s.startsWith("#") || s.length() != 7 || !isInt(s.substring(1)))
                    return Optional.of(I18n.format("config.lightoverlay-forge.invalidColor"));
                else
                    return Optional.empty();
            }).build());
            
            return builder.setSavingRunnable(() -> {
                try {
                    LightOverlayClient.saveConfig(LightOverlayClient.configFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                LightOverlayClient.loadConfig(LightOverlayClient.configFile);
            }).build();
        });
    }
    
    private static boolean isInt(String s) {
        try {
            Integer.parseInt(s, 16);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    private static int toIntColor(String str) {
        String substring = str.substring(1);
        int r = Integer.parseInt(substring.substring(0, 2), 16);
        int g = Integer.parseInt(substring.substring(2, 4), 16);
        int b = Integer.parseInt(substring.substring(4, 6), 16);
        return (r << 16) + (g << 8) + b;
    }
    
    private static String toStringColor(int toolColor) {
        String r = Integer.toHexString((toolColor >> 16) & 0xFF);
        String g = Integer.toHexString((toolColor >> 8) & 0xFF);
        String b = Integer.toHexString((toolColor >> 0) & 0xFF);
        if (r.length() == 1)
            r = "0" + r;
        if (g.length() == 1)
            g = "0" + g;
        if (b.length() == 1)
            b = "0" + b;
        return (r + g + b).toUpperCase(Locale.ROOT);
    }
}
