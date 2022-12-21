package com.contrabass.controlled;

import com.contrabass.controlled.clutch_handler.ClutchHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.joml.Vector2d;

public class ControlledInputHandler {
    
    public static Vector2d target = null;
    public static Boolean shift = null;
    public static Boolean space = null;
    public static boolean doNextRightClick = false;
    public static boolean doNextLeftClick = false;
    public static Integer switchToSlot = null;
    public static Float moveToYaw = null;
    public static Float moveToPitch = null;

    private ControlledInputHandler() {}

    private static void reset() {
        target = null;
        shift = null;
        space = null;
    }
    
    public static void handleKeys(Input keyboardInput, boolean slowDown, float factor) {
        // WASD
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        assert player != null;
        if (target != null) {
            Vector2d current = MathUtils.flatten(player.getPos());
            boolean[] keys = MathUtils.getKeysFor(current, target, player.getYaw());
            keyboardInput.pressingForward = keys[0];
            keyboardInput.pressingRight = keys[1];
            keyboardInput.pressingBack = keys[2];
            keyboardInput.pressingLeft = keys[3];
        }
        // Other
        keyboardInput.movementForward = getMovementMultiplier(keyboardInput.pressingForward, keyboardInput.pressingBack);
        keyboardInput.movementSideways = getMovementMultiplier(keyboardInput.pressingLeft, keyboardInput.pressingRight);
        keyboardInput.sneaking = shift == null ? keyboardInput.sneaking : shift;
        keyboardInput.jumping = space == null ? keyboardInput.jumping : space;
        if (slowDown) {
            keyboardInput.movementSideways *= factor;
            keyboardInput.movementForward *= factor;
        }
        reset();
    }

    public static void handleInputEvents(Runnable itemUse, Runnable attack, PlayerEntity player, MinecraftClient client) {
        for (ClutchHandler handler : ControlledClient.MLG_HANDLERS) {
            handler.handle(player, itemUse);
        }
        if (doNextRightClick) {
            itemUse.run();
            doNextRightClick = false;
        }
        if (doNextLeftClick) {
            attack.run();
            doNextLeftClick = false;
        }
        if (moveToYaw != null) {
            player.setYaw(moveToYaw);
            moveToYaw = null;
        }
        if (moveToPitch != null) {
            player.setPitch(moveToPitch);
            moveToPitch = null;
        }
        // Slot switching
        if (switchToSlot != null) {
            if (client.currentScreen == null) {
                player.getInventory().selectedSlot = switchToSlot;
            }
            switchToSlot = null;
        }
    }

    private static float getMovementMultiplier(boolean positive, boolean negative) {
        if (positive == negative) {
            return 0.0F;
        } else {
            return positive ? 1.0F : -1.0F;
        }
    }
}
