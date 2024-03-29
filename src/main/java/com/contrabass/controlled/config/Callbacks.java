package com.contrabass.controlled.config;

import com.contrabass.controlled.ControlledInit;
import com.contrabass.controlled.ControlledInputHandler;
import com.contrabass.controlled.handler.ClutchHandler;
import com.contrabass.controlled.script.Script;
import fi.dy.masa.malilib.config.options.ConfigHotkey;
import fi.dy.masa.malilib.hotkeys.KeyAction;
import fi.dy.masa.malilib.util.InfoUtils;
import net.minecraft.client.MinecraftClient;

import java.util.Map;

public class Callbacks {

    public static void init() {
        ControlledInit.LOGGER.info("Initialising callbacks");

        Hotkeys.DO_CLUTCH.getKeybind().setCallback((action, key) -> {
            ClutchHandler.doNextClutch();
            ClutchHandler.switchToBestSlot(MinecraftClient.getInstance().player);
            return true;
        });

        Hotkeys.LOCK_ROTATION.getKeybind().setCallback((action, key) -> {
            ControlledInputHandler.lockRotation();
            return true;
        });

        Hotkeys.FAST_RIGHT_CLICK.getKeybind().setCallback((action, key) -> {
            ControlledInputHandler.fastRightClick = (action == KeyAction.PRESS);
            return true;
        });

        Hotkeys.FAST_LEFT_CLICK.getKeybind().setCallback((action, key) -> {
            ControlledInputHandler.fastLeftClick = (action == KeyAction.PRESS);
            return true;
        });

        Hotkeys.PATHFIND.getKeybind().setCallback((action, key) -> {
            ControlledInputHandler.pathfind = !ControlledInputHandler.pathfind;
            InfoUtils.printBooleanConfigToggleMessage("Pathfinding", ControlledInputHandler.pathfind);
            ControlledInputHandler.setPathfindingTarget();
            return true;
        });

        for (Map.Entry<String, ConfigHotkey> entry : Hotkeys.SCRIPT_HOTKEYS.entrySet()) {
            entry.getValue().getKeybind().setCallback((action, key) -> {
                Script.get(entry.getKey()).toggle();
                return true;
            });
        }
    }
}
