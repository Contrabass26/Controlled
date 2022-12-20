package com.contrabass.controlled.clutch_handler;

import com.contrabass.controlled.ControlledClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.world.World;

import java.util.List;

public class BoatMlgHandler extends MlgHandler {

    private int stage = 0;

    public void handle(PlayerEntity player, Runnable useItem) {
        if (!player.isOnGround()) {
            if (player.getStackInHand(player.getActiveHand()).isIn(ItemTags.BOATS) && ControlledClient.doNextClutch) {
                if (isTargetingBlock(MinecraftClient.getInstance())) {
                    useItem.run();
                    stage = 1;
                }
            } else if (stage == 1) {
                useItem.run();
                stage = 0;
                ControlledClient.doNextClutch = false;
            }
        } else {
            // Failed clutch
            ControlledClient.doNextClutch = false;
        }
    }

    @Override
    public int getScore(World world, PlayerEntity player, List<ItemStack> hotbar) {
        return 0;
    }

    @Override
    public int getSlotToUse(PlayerEntity player, List<ItemStack> hotbar) {
        return findSlotFor(hotbar, ItemTags.BOATS);
    }
}
