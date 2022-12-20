package com.contrabass.controlled.clutch_handler;

import com.contrabass.controlled.ControlledClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;

import java.util.List;

public class WaterMlgHandler extends MlgHandler {

    private boolean justPlaced = false;

    public void handle(PlayerEntity player, Runnable useItem) {
        if (!player.isOnGround()) {
            if (player.getStackInHand(player.getActiveHand()).getItem() == Items.WATER_BUCKET && ControlledClient.doNextClutch) {
                if (isTargetingBlock(MinecraftClient.getInstance())) {
                    useItem.run();
                    ControlledClient.doNextClutch = false;
                    justPlaced = true;
                } else {
                    targetCentre(player);
                }
            }
        } else if (justPlaced) {
            useItem.run();
            justPlaced = false;
        }
    }

    @Override
    public int getScore(World world, PlayerEntity player, List<ItemStack> hotbar) {
        return 0;
    }

    @Override
    public int getSlotToUse(PlayerEntity player, List<ItemStack> hotbar) {
        return findSlotFor(hotbar, Items.WATER_BUCKET);
    }
}
