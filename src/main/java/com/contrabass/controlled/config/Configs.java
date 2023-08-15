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
import fi.dy.masa.malilib.config.options.ConfigInteger;
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.JsonUtils;

import java.io.File;

public class Configs implements IConfigHandler {

    private static final String CONFIG_FILE_NAME = ControlledInit.MOD_ID + ".json";

    public static class Generic {

        public static final ConfigBoolean ADJUST_CLUTCH_POSITION = new ConfigBoolean("adjustClutchPosition", true, "Whether to adjust the player's position\nwhile falling to avoid rough terrain");
        public static final ConfigInteger FAST_CLICK_CPS = new ConfigInteger("fastClickCps", 20, 1, 20, "The rough CPS to use\nfor fast right/left click");

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                ADJUST_CLUTCH_POSITION,
                FAST_CLICK_CPS
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
        ControlledInputHandler.clickThreshold = 20f / Generic.FAST_CLICK_CPS.getIntegerValue();
        ControlledInputHandler.clickCounter = (int) ControlledInputHandler.clickThreshold;
        saveToFile();
    }
}
