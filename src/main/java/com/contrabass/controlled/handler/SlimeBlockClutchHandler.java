package com.contrabass.controlled.handler;

import com.contrabass.controlled.ControlledInputHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;

import java.util.List;

public class SlimeBlockClutchHandler extends ClutchHandler {
    
    private boolean releaseSpaceNext = false;
    
    @Override
    public void handle(PlayerEntity player, Runnable useItem) {
        if (releaseSpaceNext) {
            ControlledInputHandler.jump = true;
            releaseSpaceNext = false;
        }
        if (!player.isOnGround()) {
            ItemStack stackInHand = player.getStackInHand(player.getActiveHand());
            if (stackInHand.isOf(Items.SLIME_BLOCK) && willClutchNext()) {
                if (isTargetingBlock(MinecraftClient.getInstance())) {
                    useItem.run();
                    finishClutch();
                    releaseSpaceNext = true;
                } else {
                    targetCentre(player);
                }
                ControlledInputHandler.jump = true;
            }
        } else {
            finishClutch();
        }
    }

    @Override
    public int getScore(World world, PlayerEntity player, List<ItemStack> hotbar) {
        if (getSlotToUse(player, hotbar) == -1) return 0;
        return 95;
    }

    @Override
    public int getSlotToUse(PlayerEntity player, List<ItemStack> hotbar) {
        return findSlotFor(hotbar, Items.SLIME_BLOCK);
    }
}
