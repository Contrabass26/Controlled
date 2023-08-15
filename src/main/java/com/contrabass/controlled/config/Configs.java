package com.contrabass.controlled.config;

import com.contrabass.controlled.ControlledInit;
import com.contrabass.controlled.ControlledInputHandler;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.config.ConfigUtils;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigHandler;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.config.options.ConfigDouble;
import fi.dy.masa.malilib.config.options.ConfigInteger;
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.JsonUtils;

import java.io.File;

public class Configs implements IConfigHandler {

    private static final String CONFIG_FILE_NAME = ControlledInit.MOD_ID + ".json";

    public static class Generic {

        public static final ConfigBoolean ADJUST_CLUTCH_POSITION = new ConfigBoolean("adjustClutchPosition", true, "Whether to adjust the player's position\nwhile falling to avoid rough terrain");
        public static final ConfigInteger FAST_CLICK_CPS = new ConfigInteger("fastClickCps", 20, 1, 20, true, "The rough CPS to use\nfor fast right/left click");
        public static final ConfigDouble FAST_CLICK_SHAKE_INTENSITY = new ConfigDouble("fastClickShakeIntensity", 0, 0, 5, "The intensity of screen shake\nwhile clicking fast to add realism");
        public static final ConfigInteger FAST_CLICK_SHAKE_FREQUENCY = new ConfigInteger("fastClickShakeFrequency", 50, 0, 100, true, "The percentage of ticks to\nactivate screen shake on");

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                ADJUST_CLUTCH_POSITION,
                FAST_CLICK_CPS,
                FAST_CLICK_SHAKE_INTENSITY,
                FAST_CLICK_SHAKE_FREQUENCY
        );
    }

    public static void loadFromFile() {
        File configFile = new File(FileUtils.getConfigDirectory(), CONFIG_FILE_NAME);

        if (configFile.exists() && configFile.isFile() && configFile.canRead()) {
            JsonElement element = JsonUtils.parseJsonFile(configFile);

            if (element != null && element.isJsonObject()) {
                JsonObject root = element.getAsJsonObject();

                ConfigUtils.readConfigBase(root, "Generic", Generic.OPTIONS);
                ConfigUtils.readConfigBase(root, "Hotkeys", Hotkeys.getHotkeys());
            }
        }
    }

    public static void saveToFile() {
        File dir = FileUtils.getConfigDirectory();

        if (dir.exists() && dir.isDirectory() || dir.mkdirs()) {
            JsonObject root = new JsonObject();

            ConfigUtils.writeConfigBase(root, "Generic", Generic.OPTIONS);
            ConfigUtils.writeConfigBase(root, "Hotkeys", Hotkeys.getHotkeys());

            JsonUtils.writeJsonToFile(root, new File(dir, CONFIG_FILE_NAME));
        }
    }

    @Override
    public void load() {
        loadFromFile();
    }

    @Override
    public void save() {
        ControlledInputHandler.fastClickThreshold = 20f / Generic.FAST_CLICK_CPS.getIntegerValue();
        ControlledInputHandler.fastClickCounter = (int) ControlledInputHandler.fastClickThreshold;
        ControlledInputHandler.fastClickShakeIntensity = (float) Generic.FAST_CLICK_SHAKE_INTENSITY.getDoubleValue();
        ControlledInputHandler.fastClickShakeChance = Generic.FAST_CLICK_SHAKE_FREQUENCY.getIntegerValue() / 100f;
        saveToFile();
    }
}
