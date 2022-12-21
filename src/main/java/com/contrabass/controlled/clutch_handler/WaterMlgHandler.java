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

    public void handle(PlayerEntity player, Runnable useItem) {
        if (!player.isOnGround()) {
            if (player.getStackInHand(player.getActiveHand()).getItem() == Items.WATER_BUCKET && InputHandler.doNextClutch) {
                if (isTargetingBlock(MinecraftClient.getInstance())) {
                    useItem.run();
                    InputHandler.doNextClutch = false;
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
}
