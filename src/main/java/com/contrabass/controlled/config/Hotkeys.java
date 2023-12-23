package com.contrabass.controlled.config;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.options.ConfigHotkey;
import fi.dy.masa.malilib.hotkeys.KeyAction;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Hotkeys {

    public static final KeybindSettings INGAME_BOTH_ALLOW_EXTRA = KeybindSettings.create(KeybindSettings.Context.INGAME, KeyAction.BOTH, true, true, false, true);

    public static final ConfigHotkey DO_CLUTCH = new ConfigHotkey("doClutch", "", KeybindSettings.PRESS_ALLOWEXTRA, "Do a clutch with available items");
    public static final ConfigHotkey LOCK_ROTATION = new ConfigHotkey("lockRotation", "", KeybindSettings.PRESS_ALLOWEXTRA, "Lock rotation to nearest 45°");
    public static final ConfigHotkey FAST_RIGHT_CLICK = new ConfigHotkey("fastRightClick", "", INGAME_BOTH_ALLOW_EXTRA, "Fast right click");
    public static final ConfigHotkey FAST_LEFT_CLICK = new ConfigHotkey("fastLeftClick", "", INGAME_BOTH_ALLOW_EXTRA, "Fast left click");
    public static final ConfigHotkey RECORD_MOVEMENT = new ConfigHotkey("recordMovement", "", KeybindSettings.PRESS_ALLOWEXTRA, "Start/stop recording movement");

    private static final List<ConfigHotkey> PERMANENT_HOTKEYS = ImmutableList.of(
            DO_CLUTCH,
            LOCK_ROTATION,
            FAST_RIGHT_CLICK,
            FAST_LEFT_CLICK,
            RECORD_MOVEMENT
    );

    public static final Map<String, ConfigHotkey> SCRIPT_HOTKEYS = new HashMap<>();

    public static void clearScripts() {
        SCRIPT_HOTKEYS.clear();
    }

    public static void registerScript(String name) {
        SCRIPT_HOTKEYS.put(name, new ConfigHotkey("script" + name, "", KeybindSettings.PRESS_ALLOWEXTRA, "Run script " + name));
    }

    public static List<ConfigHotkey> getHotkeys() {
        List<ConfigHotkey> hotkeys = new ArrayList<>(PERMANENT_HOTKEYS);
        hotkeys.addAll(SCRIPT_HOTKEYS.values());
        return hotkeys;
    }
}
