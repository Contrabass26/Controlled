package com.contrabass.controlled.clutch_handler;

import com.contrabass.controlled.ControlledClient;
import com.contrabass.controlled.KeyboardHandler;
import com.contrabass.controlled.MathUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.joml.Vector2d;

import java.util.List;

public class HoneyBlockSideMlgHandler extends MlgHandler {
    
    @Override
    public void handle(PlayerEntity player, Runnable useItem) {
        // TODO: Does not currently work
//        if (!player.isOnGround()) {
//            ItemStack stackInHand = player.getStackInHand(player.getActiveHand());
//            if (stackInHand.isOf(Items.HONEY_BLOCK) && ControlledClient.doNextClutch) {
//                if (isTargetingBlock(MinecraftClient.getInstance())) {
//                    useItem.run();
//                    ControlledClient.doNextClutch = false;
//                } else {
//                    adjustPos(player);
//                }
//            }
//        } else {
//            ControlledClient.doNextClutch = false;
//        }
    }

    private static void adjustPos(PlayerEntity player) {
        World world = player.world;
        // Find block to clutch on side of
        BlockPos playerPos = player.getBlockPos();
        int landingY = getTopBlock(world, playerPos);
        for (Direction direction : Direction.values()) {
            if (direction.getAxis() == Direction.Axis.Y) continue;
            // Find top block for this x and z
            int topY = getTopBlock(world, playerPos.offset(direction));
            if (topY >= landingY) {
                Vector2d start = new Vector2d(
                        MathUtils.addPlusMinus(MathUtils.roundToZero(player.getX(), 1), 0.5),
                        MathUtils.addPlusMinus(MathUtils.roundToZero(player.getZ(), 1), 0.5)
                );
                Vector2d addition = MathUtils.flatten(direction.getUnitVector()).mul(0.7375);
                KeyboardHandler.target = MathUtils.addPlusMinus(start, addition);
                ControlledClient.moveToPitch = 85f;
                ControlledClient.moveToYaw = direction.asRotation();
                break;
            }
        }
    }

    @Override
    public int getScore(World world, PlayerEntity player, List<ItemStack> hotbar) {
        return 0;
    }

    @Override
    public int getSlotToUse(PlayerEntity player, List<ItemStack> hotbar) {
        return findSlotFor(hotbar, Items.HONEY_BLOCK);
    }
}
