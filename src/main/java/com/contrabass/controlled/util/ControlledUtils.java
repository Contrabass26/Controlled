package com.contrabass.controlled.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Direction;
import org.joml.Vector2d;

public class ControlledUtils {

    private ControlledUtils() {}

    public static double getDistanceBackwards(PlayerEntity player) {
        Direction direction = Direction.fromRotation(player.getYaw() - 45).getOpposite();
        return getDistanceBackwards(player, direction);
    }

    public static double getDistanceBackwards(PlayerEntity player, Direction direction) {
        Vector2d playerPos = MathUtils.flatten(player.getPos());
        Vector2d backwards = MathUtils.roundInDirection(playerPos, direction.getOpposite());
        Vector2d differenceVector = playerPos.sub(backwards, new Vector2d());
        return Math.abs(MathUtils.getNonZeroPart(differenceVector));
    }
}
