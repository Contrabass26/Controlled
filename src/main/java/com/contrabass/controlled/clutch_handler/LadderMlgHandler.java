package com.contrabass.controlled.clutch_handler;

import com.contrabass.controlled.InputHandler;
import com.contrabass.controlled.MathUtils;
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

public class LadderMlgHandler extends MlgHandler {

    private static void adjustPos(PlayerEntity player) {
        World world = player.world;
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
            InputHandler.target = start.add(addition);
            InputHandler.moveToYaw = best.getLeft().asRotation();
        }
    }

    private static Pair<Direction, Integer> getBestDirection(World world, BlockPos playerPos) {
        int landingY = getTopBlock(world, playerPos);
        Pair<Direction, Integer> best = new Pair<>(null, Integer.MAX_VALUE);
        for (Direction direction : Direction.values()) {
            if (direction.getAxis() == Direction.Axis.Y) continue;
            // Find top block for this x and z
            int topY = getTopBlock(world, playerPos.offset(direction));
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
            if (stackInHand.isOf(Items.LADDER) && MinecraftClient.getInstance().crosshairTarget instanceof BlockHitResult hitResult && InputHandler.doNextClutch) {
                if (hitResult.getType() != HitResult.Type.MISS) {
                    useItem.run();
                    InputHandler.doNextClutch = false;
                } else {
                    adjustPos(player);
                }
            }
        } else {
            InputHandler.doNextClutch = false;
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
