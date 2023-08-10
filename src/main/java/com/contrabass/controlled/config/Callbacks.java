package com.contrabass.controlled.config;

import com.contrabass.controlled.ControlledInit;
import com.contrabass.controlled.ControlledInputHandler;
import com.contrabass.controlled.handler.ClutchHandler;
import fi.dy.masa.malilib.hotkeys.KeyAction;
import net.minecraft.client.MinecraftClient;

public class Callbacks {

    public static void init() {
        ControlledInit.LOGGER.info("Initialised callbacks");

        Hotkeys.DO_CLUTCH.getKeybind().setCallback((action, key) -> {
            ClutchHandler.doNextClutch();
            ClutchHandler.switchToBestSlot(MinecraftClient.getInstance().player);
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
    }
}
