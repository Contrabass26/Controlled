package com.contrabass.controlled.handler;

import com.contrabass.controlled.ControlledInputHandler;
import com.contrabass.controlled.util.ControlledUtils;
import com.contrabass.controlled.util.MathUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.joml.Vector2d;

import java.util.List;

public class LadderClutchHandler extends ClutchHandler {

    private static void adjustPos(PlayerEntity player) {
        World world = player.getWorld();
        // Find block to clutch on side of
        BlockPos playerPos = player.getBlockPos();
        Pair<Direction, Integer> best = getBestDirection(world, playerPos);
        // If there is a valid clutch target
        if (best.getLeft() != null) {
            Vector2d start = new Vector2d(
                    MathUtils.addPlusMinus(MathUtils.roundToZero(player.getX(), 1), 0.5),
                    MathUtils.addPlusMinus(MathUtils.roundToZero(player.getZ(), 1), 0.5)
            );
            Vector2d addition = MathUtils.flatten(best.getLeft().getOpposite().getUnitVector()).mul(0.25);
            ControlledInputHandler.target = start.add(addition);
            ControlledInputHandler.moveToYaw = best.getLeft().asRotation();
        }
    }

    private static Pair<Direction, Integer> getBestDirection(World world, BlockPos playerPos) {
        int landingY = ControlledUtils.getTopBlock(world, playerPos);
        Pair<Direction, Integer> best = new Pair<>(null, Integer.MAX_VALUE);
        for (Direction direction : Direction.Type.HORIZONTAL) {
            // Find top block for this x and z
            int topY = ControlledUtils.getTopBlock(world, playerPos.offset(direction));
            int difference = topY - landingY;
            if (difference < best.getRight() && difference > 0) {
                // Better target for ladder
                best = new Pair<>(direction, difference);
            }
        }
        return best;
    }

    public void handle(PlayerEntity player, Runnable useItem) {
        if (!player.isOnGround()) {
            ItemStack stackInHand = player.getStackInHand(player.getActiveHand());
            if (stackInHand.isOf(Items.LADDER) && MinecraftClient.getInstance().crosshairTarget instanceof BlockHitResult hitResult && willClutchNext()) {
                if (hitResult.getType() != HitResult.Type.MISS) {
                    useItem.run();
                    finishClutch();
                } else {
                    adjustPos(player);
                }
            }
        } else {
            finishClutch();
        }
    }

    @Override
    public int getScore(World world, PlayerEntity player, List<ItemStack> hotbar) {
        if (getSlotToUse(player, hotbar) == -1) return 0;
        if (getBestDirection(world, player.getBlockPos()).getLeft() == null) return 0;
        return 89;
    }

    @Override
    public int getSlotToUse(PlayerEntity player, List<ItemStack> hotbar) {
        return findSlotFor(hotbar, Items.LADDER);
    }
}
