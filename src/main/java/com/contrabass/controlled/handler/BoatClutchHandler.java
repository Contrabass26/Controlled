package com.contrabass.controlled.handler;

import com.contrabass.controlled.util.ControlledUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;

public class BoatClutchHandler extends ClutchHandler {

    private int stage = 0;

    public void handle(PlayerEntity player, Runnable useItem) {
        if (!player.isOnGround()) {
            if (player.getStackInHand(player.getActiveHand()).isIn(ItemTags.BOATS) && willClutchNext()) {
                if (ControlledUtils.isTargetingBlock(MinecraftClient.getInstance())) {
                    useItem.run();
                    stage = 1;
                }
            } else if (stage == 1) {
                useItem.run();
                stage = 0;
                finishClutch();
            }
        } else {
            // Failed clutch
            finishClutch();
        }
    }

    private boolean canPlaceBoatBelow(World world, BlockPos start) {
        int y = ControlledUtils.getTopBlock(world, start);
        for (int x = -1; x <= 1; x += 2) {
            for (int z = -1; z <= 1; z++) {
                if (ControlledUtils.getTopBlock(world, start.offset(Direction.Axis.X, x).offset(Direction.Axis.Z, z)) != y) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int getScore(World world, PlayerEntity player, List<ItemStack> hotbar) {
        if (getSlotToUse(player, hotbar) == -1) return 0;
        if (!canPlaceBoatBelow(world, player.getBlockPos())) return 0;
        if (player.getBlockY() - ControlledUtils.getTopBlock(world, player.getBlockPos()) > 20) return 0;
        return 45;
    }

    @Override
    public int getSlotToUse(PlayerEntity player, List<ItemStack> hotbar) {
        return findSlotFor(hotbar, ItemTags.BOATS);
    }
}
