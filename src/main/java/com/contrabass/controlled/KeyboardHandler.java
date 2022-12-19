package com.contrabass.controlled;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;

import java.util.Arrays;

public class KeyboardHandler {
    
    public static final Boolean[] directions = new Boolean[4];
    public static Boolean shift = null;
    public static Boolean space = null;
    
    private static void reset() {
        Arrays.fill(directions, null);
        shift = null;
        space = null;
    }
    
    public static void handle(Input keyboardInput, boolean slowDown, float factor) {
        // WASD
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        assert player != null;
        int offset = (int) ((player.getHorizontalFacing().asRotation() - 180) / 90f);
        Boolean forward = directions[addToOffset(offset, 0)];
        Boolean right = directions[addToOffset(offset, 1)];
        Boolean back = directions[addToOffset(offset, 2)];
        Boolean left = directions[addToOffset(offset, 3)];
        keyboardInput.pressingForward = forward == null ? keyboardInput.pressingForward : forward;
        keyboardInput.pressingRight = right == null ? keyboardInput.pressingRight : right;
        keyboardInput.pressingBack = back == null ? keyboardInput.pressingBack : back;
        keyboardInput.pressingLeft = left == null ? keyboardInput.pressingLeft : left;
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
