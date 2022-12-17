package com.contrabass.controlled.clutch_handler;

import com.contrabass.controlled.ControlledClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;

public class BoatMlgHandler implements MlgHandler {

    private int stage = 0;

    public void handle(PlayerEntity player, Runnable useItem) {
        if (!player.isOnGround()) {
            if (player.getStackInHand(player.getActiveHand()).isIn(ItemTags.BOATS) && MinecraftClient.getInstance().crosshairTarget instanceof BlockHitResult hitResult && ControlledClient.doNextClutch) {
                if (hitResult.getType() != HitResult.Type.MISS) {
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
}
