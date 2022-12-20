package com.contrabass.controlled;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;
import org.joml.Vector2d;

public class KeyboardHandler {
    
    public static Vector2d target = null;
    public static Boolean shift = null;
    public static Boolean space = null;
    
    private static void reset() {
        target = null;
        shift = null;
        space = null;
    }
    
    public static void handle(Input keyboardInput, boolean slowDown, float factor) {
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

    private static float getMovementMultiplier(boolean positive, boolean negative) {
        if (positive == negative) {
            return 0.0F;
        } else {
            return positive ? 1.0F : -1.0F;
        }
    }
}
