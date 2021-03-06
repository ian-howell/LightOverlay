package me.shedaniel.lightoverlay.fabric;

import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.IntegerSliderEntry;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.math.MathHelper;

import java.util.Optional;

public class LOModMenuEntry implements ModMenuApi {
    @Override
    public String getModId() {
        return "lightoverlay";
    }
    
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return this::getConfigScreenByCloth;
    }
    
    public Screen getConfigScreenByCloth(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create().setParentScreen(parent).setTitle("key.lightoverlay.category");
        
        ConfigEntryBuilder eb = builder.entryBuilder();
        ConfigCategory general = builder.getOrCreateCategory("config.lightoverlay.general");
        general.addEntry(eb.startTextDescription("§7" + I18n.translate("description.lightoverlay.caching")).build());
        general.addEntry(eb.startBooleanToggle("config.lightoverlay.caching", LightOverlay.caching).setDefaultValue(false).setSaveConsumer(bool -> LightOverlay.caching = bool).build());
        general.addEntry(eb.startIntSlider("config.lightoverlay.reach", LightOverlay.reach, 1, 64).setDefaultValue(12).setTextGetter(integer -> "Reach: " + integer + " Blocks").setSaveConsumer(integer -> LightOverlay.reach = integer).build());
        IntegerSliderEntry crossLevel = eb.startIntSlider("config.lightoverlay.crossLevel", LightOverlay.crossLevel, 0, 15).setDefaultValue(7).setTextGetter(integer -> "Cross Level: " + integer).setSaveConsumer(integer -> LightOverlay.crossLevel = integer).build();
        general.addEntry(crossLevel);
        general.addEntry(eb.startIntSlider("config.lightoverlay.secondaryLevel", LightOverlay.secondaryLevel, -1, 15)
                .setErrorSupplier(integer -> {
                    if (integer >= 0 && integer >= crossLevel.getValue()) return Optional.of("Secondary Level cannot be higher than Cross Level!");
                    return Optional.empty();
                }).setDefaultValue(-1).setTextGetter(integer -> integer < 0 ? "Off" : "Level: " + integer).setSaveConsumer(integer -> LightOverlay.secondaryLevel = integer).build());
        general.addEntry(eb.startBooleanToggle("config.lightoverlay.showNumber", LightOverlay.showNumber).setDefaultValue(false).setSaveConsumer(bool -> LightOverlay.showNumber = bool).build());
        general.addEntry(eb.startBooleanToggle("config.lightoverlay.smoothLines", LightOverlay.smoothLines).setDefaultValue(true).setSaveConsumer(bool -> LightOverlay.smoothLines = bool).build());
        general.addEntry(eb.startBooleanToggle("config.lightoverlay.underwater", LightOverlay.underwater).setDefaultValue(false).setSaveConsumer(bool -> LightOverlay.underwater = bool).build());
        general.addEntry(eb.startIntSlider("config.lightoverlay.lineWidth", MathHelper.floor(LightOverlay.lineWidth * 100), 100, 700).setDefaultValue(100).setTextGetter(integer -> "Light Width: " + LightOverlay.FORMAT.format(integer / 100d)).setSaveConsumer(integer -> LightOverlay.lineWidth = integer / 100f).build());
        general.addEntry(eb.startColorField("config.lightoverlay.yellowColor", LightOverlay.yellowColor).setDefaultValue(0xFFFF00).setSaveConsumer(color -> LightOverlay.yellowColor = color).build());
        general.addEntry(eb.startColorField("config.lightoverlay.redColor", LightOverlay.redColor).setDefaultValue(0xFF0000).setSaveConsumer(color -> LightOverlay.redColor = color).build());
        general.addEntry(eb.startColorField("config.lightoverlay.secondaryColor", LightOverlay.secondaryColor).setDefaultValue(0x0000FF).setSaveConsumer(color -> LightOverlay.secondaryColor = color).build());
        
        return builder.setSavingRunnable(() -> {
            try {
                LightOverlay.saveConfig(LightOverlay.configFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
            LightOverlay.loadConfig(LightOverlay.configFile);
        }).build();
    }
}
