package com.contrabass.controlled;

import com.contrabass.controlled.clutch_handler.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ControlledClient implements ClientModInitializer {

    public static final List<MlgHandler> MLG_HANDLERS;
    static {
        MLG_HANDLERS = new ArrayList<>();
        MLG_HANDLERS.add(new BoatMlgHandler());
        MLG_HANDLERS.add(new HayBlockMlgHandler());
        MLG_HANDLERS.add(new HoneyBlockMlgHandler());
        MLG_HANDLERS.add(new HoneyBlockSideMlgHandler());
        MLG_HANDLERS.add(new LadderMlgHandler());
        MLG_HANDLERS.add(new ScaffoldingMlgHandler());
        MLG_HANDLERS.add(new SlimeBlockMlgHandler());
        MLG_HANDLERS.add(new SweetBerriesMlgHandler());
        MLG_HANDLERS.add(new TwistingVinesMlgHandler());
        MLG_HANDLERS.add(new WaterMlgHandler());
    }

    public static boolean doNextClutch = false;
    public static boolean doNextRightClick = false;
    public static boolean doNextLeftClick = false;
    public static Integer switchToSlot = null;
    public static Float moveToYaw = null;
    public static Float moveToPitch = null;

    private static final String KEYBIND_GROUP = "category.controlled.controlled";
    private static final KeyBinding MLG_KEYBINDING = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.controlled.mlg", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_M, KEYBIND_GROUP));
    private static final KeyBinding FAST_RIGHT_CLICK_KEYBINDING = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.controlled.fast_right_click", InputUtil.Type.KEYSYM, GLFW.GLFW_MOUSE_BUTTON_4, KEYBIND_GROUP));
    private static final KeyBinding FAST_LEFT_CLICK_KEYBINDING = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.controlled.fast_left_click", InputUtil.Type.KEYSYM, GLFW.GLFW_MOUSE_BUTTON_5, KEYBIND_GROUP));
    private static final KeyBinding LOCK_ROTATION_KEYBINDING = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.controlled.lock_rotation", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, KEYBIND_GROUP));

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ClientPlayerEntity player = client.player;
            assert player != null;
            while (MLG_KEYBINDING.wasPressed()) {
                doNextClutch = true;
                // Switch to best slot
                PlayerInventory inventory = player.getInventory();
                List<ItemStack> hotbar = IntStream.range(0, 9).mapToObj(inventory::getStack).collect(Collectors.toList());
                Pair<MlgHandler, Integer> best = new Pair<>(null, 0);
                for (MlgHandler handler : MLG_HANDLERS) {
                    int score = handler.getScore(player.world, player, hotbar);
                    if (score > best.getRight()) {
                        best = new Pair<>(handler, score);
                    }
                }
                if (best.getRight() != 0) {
                    switchToSlot = best.getLeft().getSlotToUse(player, hotbar);
                }
            }
            if (FAST_RIGHT_CLICK_KEYBINDING.isPressed()) {
                doNextRightClick = true;
            }
            if (FAST_LEFT_CLICK_KEYBINDING.isPressed()) {
                doNextLeftClick = true;
            }
            if (LOCK_ROTATION_KEYBINDING.isPressed()) {
                float yaw = player.getYaw();
                float newYaw = Math.round(yaw / 45f) * 45f;
                player.setYaw(newYaw);
            }
            if (moveToYaw != null) {
                player.setYaw(moveToYaw);
                moveToYaw = null;
            }
            if (moveToPitch != null) {
                player.setPitch(moveToPitch);
                moveToPitch = null;
            }
        });
    }
}
