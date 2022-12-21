package com.contrabass.controlled.clutch_handler;

import com.contrabass.controlled.InputHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionTypes;

import java.util.List;

public class WaterMlgHandler extends MlgHandler {

    private boolean justPlaced = false;
//    private Vector2d cachedTarget = null;

    public void handle(PlayerEntity player, Runnable useItem) {
        if (!player.isOnGround()) {
            if (player.getStackInHand(player.getActiveHand()).getItem() == Items.WATER_BUCKET && MlgHandler.willClutchNext()) {
                if (isTargetingBlock(MinecraftClient.getInstance())) {
                    useItem.run();
                    MlgHandler.finishClutch();
                    justPlaced = true;
                } else {
                    doTargeting(player);
                }
            }
        } else if (justPlaced) {
            useItem.run();
            justPlaced = false;
        }
    }

    private void doTargeting(PlayerEntity player) {
//        if (cachedTarget == null) {
//            cachedTarget = targetCentre(player);
//        }
//        InputHandler.target = cachedTarget;
        InputHandler.target = targetCentre(player);
    }

    @Override
    public int getScore(World world, PlayerEntity player, List<ItemStack> hotbar) {
        if (getSlotToUse(player, hotbar) == -1) return 0;
        if (world.getDimensionKey() == DimensionTypes.THE_NETHER) return 0;
        BlockPos topBlockPos = getTopBlockPos(world, player.getBlockPos());
        if (world.getBlockState(topBlockPos).isSolidBlock(world, topBlockPos)) return 100;
        return 50;
    }

    @Override
    public int getSlotToUse(PlayerEntity player, List<ItemStack> hotbar) {
        return findSlotFor(hotbar, Items.WATER_BUCKET);
    }

//    @Override
//    protected void clearCaches() {
//        cachedTarget = null;
//    }
}
