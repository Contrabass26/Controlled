package com.contrabass.controlled.clutch_handler;

import com.contrabass.controlled.ControlledClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;

public class LadderMlgHandler extends BlockMlgHandler {

    @Override
    protected void adjustPos(PlayerEntity player) {

    }

    public void handle(PlayerEntity player, Runnable useItem) {
        if (!player.isOnGround()) {
            ItemStack stackInHand = player.getStackInHand(player.getActiveHand());
            if (stackInHand.isOf(Items.LADDER) && MinecraftClient.getInstance().crosshairTarget instanceof BlockHitResult hitResult && ControlledClient.doNextClutch) {
                if (hitResult.getType() != HitResult.Type.MISS) {
                    useItem.run();
                    ControlledClient.doNextClutch = false;
                } else {
                    adjustPos(player);
                }
            }
        } else {
            ControlledClient.doNextClutch = false;
        }
    }
}
