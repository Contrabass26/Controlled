package com.contrabass.controlled.handler;

import com.contrabass.controlled.ControlledInputHandler;
import com.contrabass.controlled.util.ControlledUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class ScaffoldingClutchHandler extends ClutchHandler {

    private boolean releaseShiftNext = false;

    @Override
    public void handle(PlayerEntity player, Runnable useItem) {
        if (releaseShiftNext) {
            ControlledInputHandler.shift = true;
            releaseShiftNext = false;
        }
        if (!player.isOnGround()) {
            ItemStack stackInHand = player.getStackInHand(player.getActiveHand());
            if (stackInHand.isOf(Items.SCAFFOLDING) && willClutchNext()) {
                if (ControlledUtils.isTargetingBlock(MinecraftClient.getInstance())) {
                    useItem.run();
                    finishClutch();
                    releaseShiftNext = true;
                } else {
                    targetCentre(player);
                }
                ControlledInputHandler.shift = true;
            }
        } else {
            finishClutch();
        }
    }

    @Override
    public int getScore(World world, PlayerEntity player, List<ItemStack> hotbar) {
        if (getSlotToUse(player, hotbar) == -1) return 0;
        BlockPos topBlockPos = ControlledUtils.getTopBlockPos(world, player.getBlockPos());
        if (!world.getBlockState(topBlockPos).isSolidBlock(world, topBlockPos)) return 0;
        // TODO: One block of scaffolding only breaks falls up to a certain height
        return 90;
    }

    @Override
    public int getSlotToUse(PlayerEntity player, List<ItemStack> hotbar) {
        return findSlotFor(hotbar, Items.SCAFFOLDING);
    }
}
