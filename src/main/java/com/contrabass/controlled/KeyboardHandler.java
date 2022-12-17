package com.contrabass.controlled;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;

import java.util.Arrays;

public class KeyboardHandler {
    
    public static final boolean[] directions = new boolean[4];
    public static boolean shift = false;
    public static boolean space = false;
    
    private static void reset() {
        Arrays.fill(directions, false);
        shift = false;
        space = false;
    }
    
    public static void handle(Input keyboardInput, boolean slowDown, float factor) {
        // WASD
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        assert player != null;
        int offset = (int) ((player.getHorizontalFacing().asRotation() - 180) / 90f);
        keyboardInput.pressingForward = keyboardInput.pressingForward || directions[addToOffset(offset, 0)];
        keyboardInput.pressingRight = keyboardInput.pressingRight || directions[addToOffset(offset, 1)];
        keyboardInput.pressingBack = keyboardInput.pressingBack || directions[addToOffset(offset, 2)];
        keyboardInput.pressingLeft = keyboardInput.pressingLeft || directions[addToOffset(offset, 3)];
        // Other
        keyboardInput.movementForward = getMovementMultiplier(keyboardInput.pressingForward, keyboardInput.pressingBack);
        keyboardInput.movementSideways = getMovementMultiplier(keyboardInput.pressingLeft, keyboardInput.pressingRight);
        keyboardInput.sneaking = keyboardInput.sneaking || shift;
        keyboardInput.jumping = keyboardInput.jumping || space;
        if (slowDown) {
            keyboardInput.movementSideways *= factor;
            keyboardInput.movementForward *= factor;
        }
        reset();
    }

    private static int addToOffset(int offset, int increment) {
        int result = (offset + increment) % 4;
        return result >= 0 ? result : Math.abs(-4 - result);
    }

    private static float getMovementMultiplier(boolean positive, boolean negative) {
        if (positive == negative) {
            return 0.0F;
        } else {
            return positive ? 1.0F : -1.0F;
        }
    }
}
