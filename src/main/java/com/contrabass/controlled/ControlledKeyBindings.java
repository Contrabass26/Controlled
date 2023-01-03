package com.contrabass.controlled;

import com.contrabass.controlled.handler.ClutchHandler;
import com.contrabass.controlled.handler.ShiftBridgeHandler;
import com.contrabass.controlled.script.Script;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import org.lwjgl.glfw.GLFW;

public class ControlledKeyBindings {

    private static final String KEYBIND_GROUP = "category.controlled.controlled";
    private static final KeyBinding MLG_KEYBINDING = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.controlled.mlg", InputUtil.Type.KEYSYM, GLFW.GLFW_MOUSE_BUTTON_5, KEYBIND_GROUP));
    private static final KeyBinding FAST_RIGHT_CLICK_KEYBINDING = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.controlled.fast_right_click", InputUtil.Type.KEYSYM, GLFW.GLFW_MOUSE_BUTTON_4, KEYBIND_GROUP));
    private static final KeyBinding FAST_LEFT_CLICK_KEYBINDING = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.controlled.fast_left_click", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_I, KEYBIND_GROUP));
    private static final KeyBinding LOCK_ROTATION_KEYBINDING = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.controlled.lock_rotation", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_G, KEYBIND_GROUP));
    private static final KeyBinding SHIFT_BRIDGE_KEYBINDING = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.controlled.shift_bridge", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, KEYBIND_GROUP));
    private static final KeyBinding TEST_SCRIPT_KEYBINDING = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.controlled.test_script", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_H, KEYBIND_GROUP));

    private static boolean scriptConsumed = false;

    private ControlledKeyBindings() {}

    public static void init() {
        ControlledInit.LOGGER.info("Registered keybinds for " + KEYBIND_GROUP);
    }

    public static void handleKeyBindings(PlayerEntity player) {
        while (MLG_KEYBINDING.wasPressed()) {
            ClutchHandler.doNextClutch();
            ClutchHandler.switchToBestSlot(player);
        }
        if (FAST_RIGHT_CLICK_KEYBINDING.isPressed()) {
            ControlledInputHandler.doNextRightClick = true;
        }
        if (FAST_LEFT_CLICK_KEYBINDING.isPressed()) {
            ControlledInputHandler.doNextLeftClick = true;
        }
        if (LOCK_ROTATION_KEYBINDING.isPressed()) {
            ControlledInputHandler.lockRotation(player);
        }
        while (SHIFT_BRIDGE_KEYBINDING.wasPressed()) {
            ShiftBridgeHandler.toggleActivated();
        }
        if (TEST_SCRIPT_KEYBINDING.wasPressed()) {
            if (!scriptConsumed) {
                Script.get("test").toggleRunning();
                scriptConsumed = true;
            }
        } else scriptConsumed = false;
    }
}
