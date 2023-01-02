package com.contrabass.controlled.handler;

import com.contrabass.controlled.ControlledInputHandler;
import com.contrabass.controlled.ControlledKeyBindings;
import com.contrabass.controlled.InputModifier;
import com.contrabass.controlled.MathUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;
import org.joml.Vector2d;

public class ShiftBridgeHandler {

    private static final String MODIFIER_KEY = "shift_bridge";

    public static boolean activated = false;

    public static void tick(PlayerEntity player) {
        if (activated) {
            Direction bridgeDirection = Direction.fromRotation(player.getYaw() - 45).getOpposite();
            // Rotation
            ControlledKeyBindings.lockRotation(player);
            ControlledInputHandler.moveToPitch = 78f;
            // Click
            if (MinecraftClient.getInstance().crosshairTarget instanceof BlockHitResult blockHitResult && blockHitResult.getSide() == bridgeDirection) {
                ControlledInputHandler.doNextRightClick = true;
            }
            // Shifting
            Vector2d playerPos = MathUtils.flatten(player.getPos());
            Vector2d backwards = MathUtils.roundInDirection(playerPos, bridgeDirection.getOpposite());
            Vector2d differenceVector = playerPos.sub(backwards, new Vector2d());
            double difference = Math.abs(MathUtils.getNonZeroPart(differenceVector));
            ControlledInputHandler.shift = !(difference > 0.25 && difference < 0.9);
        }
    }

    public static void toggleActivated() {
        activated = !activated;
        if (activated) {
            ControlledInputHandler.addInputModifier(InputModifier.s(true, 0, MODIFIER_KEY));
            ControlledInputHandler.addInputModifier(InputModifier.d(true, 0, MODIFIER_KEY));
        } else {
            ControlledInputHandler.removeInputModifier(s -> s.equals(MODIFIER_KEY));
        }
    }
}
