package com.contrabass.controlled.handler;

import com.contrabass.controlled.ControlledInputHandler;
import com.contrabass.controlled.InputModifier;
import com.contrabass.controlled.util.ControlledUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;

public class ShiftBridgeHandler {

    private static final String MODIFIER_KEY = "shift_bridge";

    public static boolean activated = false;

    public static void tick(PlayerEntity player) {
        if (activated) {
            Direction bridgeDirection = Direction.fromRotation(player.getYaw() - 45).getOpposite();
            // Rotation
            ControlledInputHandler.lockRotation(player);
            ControlledInputHandler.moveToPitch = 78f;
            // Click
            if (MinecraftClient.getInstance().crosshairTarget instanceof BlockHitResult blockHitResult && blockHitResult.getSide() == bridgeDirection) {
                ControlledInputHandler.doNextRightClick = 1;
            }
            // Shifting
            double difference = ControlledUtils.getDistanceBackwards(player.getPos(), bridgeDirection);
            ControlledInputHandler.shift = !(difference > 0.25 && difference < 0.9);
        }
    }

    public static void toggleActivated() {
        activated = !activated;
        if (activated) {
            ControlledInputHandler.addInputModifier(InputModifier.Movement.s(true, 0, MODIFIER_KEY));
            ControlledInputHandler.addInputModifier(InputModifier.Movement.d(true, 0, MODIFIER_KEY));
        } else {
            ControlledInputHandler.removeInputModifier(s -> s.equals(MODIFIER_KEY));
        }
    }
}
