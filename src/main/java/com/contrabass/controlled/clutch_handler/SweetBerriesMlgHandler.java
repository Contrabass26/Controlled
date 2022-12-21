package com.contrabass.controlled.clutch_handler;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SweetBerriesMlgHandler extends MlgHandler {

    private static final Set<Block> PLACEABLE;
    static {
        PLACEABLE = new HashSet<>();
        PLACEABLE.add(Blocks.GRASS_BLOCK);
        PLACEABLE.add(Blocks.DIRT);
        PLACEABLE.add(Blocks.COARSE_DIRT);
        PLACEABLE.add(Blocks.PODZOL);
        PLACEABLE.add(Blocks.FARMLAND);
        PLACEABLE.add(Blocks.MOSS_BLOCK);
    }

    @Override
    public void handle(PlayerEntity player, Runnable useItem) {
        if (!player.isOnGround()) {
            ItemStack stackInHand = player.getStackInHand(player.getActiveHand());
            if (stackInHand.isOf(Items.SWEET_BERRIES) && willClutchNext()) {
                if (isTargetingBlock(MinecraftClient.getInstance())) {
                    useItem.run();
                    finishClutch();
                } else {
                    targetCentre(player);
                }
            }
        } else {
            finishClutch();
        }
    }

    @Override
    public int getScore(World world, PlayerEntity player, List<ItemStack> hotbar) {
        if (getSlotToUse(player, hotbar) == -1) return 0;
        if (!PLACEABLE.contains(world.getBlockState(getTopBlockPos(world, player.getBlockPos())).getBlock())) return 0;
        return 85;
    }

    @Override
    public int getSlotToUse(PlayerEntity player, List<ItemStack> hotbar) {
        return findSlotFor(hotbar, Items.SWEET_BERRIES);
    }
}
