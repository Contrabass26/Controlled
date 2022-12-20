package com.contrabass.controlled.clutch_handler;

import com.contrabass.controlled.ControlledClient;
import com.contrabass.controlled.KeyboardHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class ScaffoldingMlgHandler extends MlgHandler {

    private boolean releaseShiftNext = false;

    @Override
    public void handle(PlayerEntity player, Runnable useItem) {
        if (releaseShiftNext) {
            KeyboardHandler.shift = true;
            releaseShiftNext = false;
        }
        if (!player.isOnGround()) {
            ItemStack stackInHand = player.getStackInHand(player.getActiveHand());
            if (stackInHand.isOf(Items.SCAFFOLDING) && ControlledClient.doNextClutch) {
                if (isTargetingBlock(MinecraftClient.getInstance())) {
                    useItem.run();
                    ControlledClient.doNextClutch = false;
                    releaseShiftNext = true;
                } else {
                    targetCentre(player);
                }
                KeyboardHandler.shift = true;
            }
        } else {
            ControlledClient.doNextClutch = false;
        }
    }

    @Override
    public int getScore(World world, PlayerEntity player, List<ItemStack> hotbar) {
        if (getSlotToUse(player, hotbar) == -1) return 0;
        BlockPos topBlockPos = getTopBlockPos(world, player.getBlockPos());
        if (!world.getBlockState(topBlockPos).isSolidBlock(world, topBlockPos)) return 0;
        // TODO: One block of scaffolding only breaks falls up to a certain height
        return 90;
    }

    @Override
    public int getSlotToUse(PlayerEntity player, List<ItemStack> hotbar) {
        return findSlotFor(hotbar, Items.SCAFFOLDING);
    }
}
