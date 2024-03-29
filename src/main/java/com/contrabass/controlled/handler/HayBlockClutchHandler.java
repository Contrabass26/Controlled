package com.contrabass.controlled.handler;

import com.contrabass.controlled.util.ControlledUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;

import java.util.List;

public class HayBlockClutchHandler extends ClutchHandler {

    @Override
    public void handle(PlayerEntity player, Runnable useItem) {
        if (!player.isOnGround()) {
            ItemStack stackInHand = player.getStackInHand(player.getActiveHand());
            if (stackInHand.isOf(Items.HAY_BLOCK) && willClutchNext()) {
                if (ControlledUtils.isTargetingBlock(MinecraftClient.getInstance())) {
                    useItem.run();
                    finishClutch();
                } else {
                    targetCentre(player);
                }
            }
        } else {
            finishClutch();
        }
    }

    @Override
    public int getScore(World world, PlayerEntity player, List<ItemStack> hotbar) {
        if (getSlotToUse(player, hotbar) == -1) return 0;
        float fallDistance = player.getBlockY() - ControlledUtils.getTopBlock(world, player.getBlockPos()) + player.fallDistance;
        if (fallDistance > 103) return 0;
        return 45;
    }

    @Override
    public int getSlotToUse(PlayerEntity player, List<ItemStack> hotbar) {
        return findSlotFor(hotbar, Items.HAY_BLOCK);
    }
}
