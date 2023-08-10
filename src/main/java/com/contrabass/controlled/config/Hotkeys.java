package com.contrabass.controlled.config;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.options.ConfigHotkey;
import fi.dy.masa.malilib.hotkeys.KeyAction;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;

import java.util.List;

public class Hotkeys {

    public static final KeybindSettings INGAME_BOTH_ALLOW_EXTRA = KeybindSettings.create(KeybindSettings.Context.INGAME, KeyAction.BOTH, true, true, false, true);

    public static final ConfigHotkey DO_CLUTCH = new ConfigHotkey("doClutch", "", KeybindSettings.PRESS_ALLOWEXTRA, "Do a clutch with available items");
    public static final ConfigHotkey LOCK_ROTATION = new ConfigHotkey("lockRotation", "", KeybindSettings.PRESS_ALLOWEXTRA, "Lock rotation to nearest 45°");
    public static final ConfigHotkey FAST_RIGHT_CLICK = new ConfigHotkey("fastRightClick", "", INGAME_BOTH_ALLOW_EXTRA, "Fast right click (20 cps)");
    public static final ConfigHotkey FAST_LEFT_CLICK = new ConfigHotkey("fastLeftClick", "", INGAME_BOTH_ALLOW_EXTRA, "Fast left click (20 cps)");

    public static final List<ConfigHotkey> HOTKEY_LIST = ImmutableList.of(
            DO_CLUTCH,
            LOCK_ROTATION,
            FAST_RIGHT_CLICK,
            FAST_LEFT_CLICK
    );
}
