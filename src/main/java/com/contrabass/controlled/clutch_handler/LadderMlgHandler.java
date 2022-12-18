package com.contrabass.controlled.clutch_handler;

import com.contrabass.controlled.ControlledClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import org.joml.Vector3f;

public class LadderMlgHandler extends BlockMlgHandler {

    private static void target(PlayerEntity player) {
        Vector3f v = player.getHorizontalFacing().getUnitVector();
        BlockMlgHandler.targetFixedPoint(player,
                v.x == 0 ? 0.5 : normalise(v.x * 0.75),
                v.z == 0 ? 0.5 : normalise(v.z * 0.75));
    }

    private static double normalise(double d) {
        return d < 0 ? (d + 1) : d;
    }

    @Override
    protected void adjustPos(PlayerEntity player) {
        target(player);
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
