package com.contrabass.controlled.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
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

    public static boolean isTargetingBlock(MinecraftClient minecraft) {
        if (minecraft.crosshairTarget instanceof BlockHitResult blockHitResult) {
            return blockHitResult.getType() != HitResult.Type.MISS;
        }
        return false;
    }

    public static int getTopBlock(World world, BlockPos start) {
        while (!world.getBlockState(start).isSolidBlock(world, start) && start.getY() != -64) {
            start = start.down();
        }
        return start.getY();
    }

    public static BlockPos getTopBlockPos(World world, BlockPos start) {
        return new BlockPos(start.getX(), getTopBlock(world, start), start.getZ());
    }
}
