package com.contrabass.controlled.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector2d;

public class ControlledUtils {

    private ControlledUtils() {}

    public static double getDistanceBackwards(PlayerEntity player) {
        Direction direction = Direction.fromRotation(player.getYaw() - 45).getOpposite();
        return getDistanceBackwards(player.getPos(), direction);
    }

    public static double getDistanceBackwards(Vec3d pos, Direction direction) {
        Vector2d playerPos = MathUtils.flatten(pos);
        Vector2d backwards = MathUtils.roundInDirection(playerPos, direction.getOpposite());
        Vector2d differenceVector = playerPos.sub(backwards, new Vector2d());
        return Math.abs(MathUtils.getNonZeroPart(differenceVector));
    }
}
