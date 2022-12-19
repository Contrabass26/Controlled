package com.contrabass.controlled.clutch_handler;

import com.contrabass.controlled.ControlledClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;

public class WaterMlgHandler implements MlgHandler {

    private boolean justPlaced = false;

    public void handle(PlayerEntity player, Runnable useItem) {
        if (!player.isOnGround()) {
            if (player.getStackInHand(player.getActiveHand()).getItem() == Items.WATER_BUCKET && MinecraftClient.getInstance().crosshairTarget instanceof BlockHitResult hitResult && ControlledClient.doNextClutch) {
                if (hitResult.getType() != HitResult.Type.MISS) {
                    useItem.run();
                    ControlledClient.doNextClutch = false;
                    justPlaced = true;
                } else {
                    BlockMlgHandler.targetCentre(player);
                }
            }
        } else if (justPlaced) {
            useItem.run();
            justPlaced = false;
        }
    }
}
