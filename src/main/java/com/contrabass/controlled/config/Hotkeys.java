package com.contrabass.controlled.config;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.options.ConfigHotkey;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;

import java.util.List;

public class Hotkeys {

    public static final ConfigHotkey DO_CLUTCH = new ConfigHotkey("doClutch", "", KeybindSettings.PRESS_ALLOWEXTRA, "Do a clutch with available items");
    public static final ConfigHotkey LOCK_ROTATION = new ConfigHotkey("lockRotation", "", KeybindSettings.PRESS_ALLOWEXTRA, "Lock rotation to nearest 45°");
    public static final ConfigHotkey FAST_RIGHT_CLICK = new ConfigHotkey("fastRightClick", "", KeybindSettings.INGAME_BOTH, "Fast right click");

    public static final List<ConfigHotkey> HOTKEY_LIST = ImmutableList.of(
            DO_CLUTCH,
            LOCK_ROTATION,
            FAST_RIGHT_CLICK
    );
}
