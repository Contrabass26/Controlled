package com.contrabass.controlled.clutch_handler;

import com.contrabass.controlled.ControlledClient;
import com.contrabass.controlled.KeyboardHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;

import java.util.List;

public class SlimeBlockMlgHandler extends MlgHandler {
    
    private boolean releaseSpaceNext = false;
    
    @Override
    public void handle(PlayerEntity player, Runnable useItem) {
        if (releaseSpaceNext) {
            KeyboardHandler.space = true;
            releaseSpaceNext = false;
        }
        if (!player.isOnGround()) {
            ItemStack stackInHand = player.getStackInHand(player.getActiveHand());
            if (stackInHand.isOf(Items.SLIME_BLOCK) && ControlledClient.doNextClutch) {
                if (isTargetingBlock(MinecraftClient.getInstance())) {
                    useItem.run();
                    ControlledClient.doNextClutch = false;
                    releaseSpaceNext = true;
                } else {
                    targetCentre(player);
                }
                KeyboardHandler.space = true;
            }
        } else {
            ControlledClient.doNextClutch = false;
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
