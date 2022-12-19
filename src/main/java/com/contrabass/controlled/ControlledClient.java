package com.contrabass.controlled;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ControlledClient implements ClientModInitializer {

    public static boolean doNextClutch = false;
    public static boolean doNextRightClick = false;
    public static boolean doNextLeftClick = false;

    private static final String KEYBIND_GROUP = "category.controlled.controlled";
    private static final KeyBinding MLG_KEYBINDING = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.controlled.mlg", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_M, KEYBIND_GROUP));
    private static final KeyBinding FAST_RIGHT_CLICK_KEYBINDING = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.controlled.fast_right_click", InputUtil.Type.KEYSYM, GLFW.GLFW_MOUSE_BUTTON_4, KEYBIND_GROUP));
    private static final KeyBinding FAST_LEFT_CLICK_KEYBINDING = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.controlled.fast_left_click", InputUtil.Type.KEYSYM, GLFW.GLFW_MOUSE_BUTTON_5, KEYBIND_GROUP));
    private static final KeyBinding LOCK_ROTATION_KEYBINDING = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.controlled.lock_rotation", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, KEYBIND_GROUP));

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (MLG_KEYBINDING.wasPressed()) {
                doNextClutch = true;
            }
            if (FAST_RIGHT_CLICK_KEYBINDING.isPressed()) {
                doNextRightClick = true;
            }
            if (FAST_LEFT_CLICK_KEYBINDING.isPressed()) {
                doNextLeftClick = true;
            }
            if (LOCK_ROTATION_KEYBINDING.isPressed()) {
                ClientPlayerEntity player = client.player;
                assert player != null;
                float yaw = player.getYaw();
                float newYaw = Math.round(yaw / 45f) * 45f;
                player.setYaw(newYaw);
            }
        });
    }
}
