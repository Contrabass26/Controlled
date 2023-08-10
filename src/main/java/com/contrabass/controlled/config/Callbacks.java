package com.contrabass.controlled.config;

import com.contrabass.controlled.ControlledInit;
import com.contrabass.controlled.ControlledInputHandler;
import com.contrabass.controlled.handler.WaterClutchHandler;
import com.contrabass.controlled.script.Script;
import fi.dy.masa.malilib.config.options.ConfigHotkey;
import fi.dy.masa.malilib.hotkeys.KeyAction;
import net.minecraft.client.MinecraftClient;

import java.util.Map;

public class Callbacks {

    public static void init() {
        ControlledInit.LOGGER.info("Initialising callbacks");

        Hotkeys.DO_CLUTCH.getKeybind().setCallback((action, key) -> {
            WaterClutchHandler.doNextClutch();
            return true;
        });

        Hotkeys.LOCK_ROTATION.getKeybind().setCallback(((action, key) -> {
            ControlledInputHandler.lockRotation();
            return true;
        }));

        Hotkeys.FAST_RIGHT_CLICK.getKeybind().setCallback(((action, key) -> {
            ControlledInputHandler.fastRightClick = (action == KeyAction.PRESS);
            return true;
        }));

        Hotkeys.FAST_LEFT_CLICK.getKeybind().setCallback(((action, key) -> {
            ControlledInputHandler.fastLeftClick = (action == KeyAction.PRESS);
            return true;
        }));

        for (Map.Entry<String, ConfigHotkey> entry : Hotkeys.SCRIPT_HOTKEYS.entrySet()) {
            entry.getValue().getKeybind().setCallback(((action, key) -> {
                Script.get(entry.getKey()).toggle();
                return true;
            }));
        }
    }
}
