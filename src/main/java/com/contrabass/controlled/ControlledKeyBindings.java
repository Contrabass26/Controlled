package com.contrabass.controlled;

import com.contrabass.controlled.gui.GuiConfigs;
import com.contrabass.controlled.script.Script;
import fi.dy.masa.malilib.gui.GuiBase;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ControlledKeyBindings {

    private static final String KEYBIND_GROUP = "category.controlled.controlled";
    private static final KeyBinding FAST_LEFT_CLICK_KEYBINDING = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.controlled.fast_left_click", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_I, KEYBIND_GROUP));
    private static final KeyBinding SHIFT_BRIDGE_KEYBINDING = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.controlled.shift_bridge", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, KEYBIND_GROUP));
    private static final KeyBinding UPWARD_BRIDGE_KEYBINDING = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.controlled.upward_bridge", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, KEYBIND_GROUP));
    private static final KeyBinding CONFIG_OPEN = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.controlled.config", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_G, KEYBIND_GROUP));

    private ControlledKeyBindings() {}

    public static void init() {
        ControlledInit.LOGGER.info("Registered keybinds for " + KEYBIND_GROUP);
    }

    public static void handleKeyBindings() {
        if (FAST_LEFT_CLICK_KEYBINDING.isPressed()) {
            ControlledInputHandler.doNextLeftClick = 1;
        }
        if (CONFIG_OPEN.wasPressed()) {
            ControlledInit.LOGGER.info("Opening Controlled config");
            GuiBase.openGui(new GuiConfigs());
        }
        Script.get("ShiftBridgeScript").handleKeybind(SHIFT_BRIDGE_KEYBINDING.isPressed());
        Script.get("UpwardBridgeScript").handleKeybind(UPWARD_BRIDGE_KEYBINDING.isPressed());
    }
}
