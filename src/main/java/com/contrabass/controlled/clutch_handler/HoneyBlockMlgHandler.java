package com.contrabass.controlled.clutch_handler;

import com.contrabass.controlled.ControlledClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;

import java.util.List;

public class HoneyBlockMlgHandler extends MlgHandler {

    @Override
    public void handle(PlayerEntity player, Runnable useItem) {
        if (!player.isOnGround()) {
            ItemStack stackInHand = player.getStackInHand(player.getActiveHand());
            if (stackInHand.isOf(Items.HONEY_BLOCK) && ControlledClient.doNextClutch) {
                if (isTargetingBlock(MinecraftClient.getInstance())) {
                    useItem.run();
                    ControlledClient.doNextClutch = false;
                } else {
                    targetCentre(player);
                }
            }
        } else {
            ControlledClient.doNextClutch = false;
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
