package com.contrabass.controlled.clutch_handler;

import com.contrabass.controlled.ControlledClient;
import com.contrabass.controlled.KeyboardHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public abstract class BlockMlgHandler implements MlgHandler {

    private static final List<Item> CLUTCH_WHITELIST;
    static {
        CLUTCH_WHITELIST = new ArrayList<>();
        CLUTCH_WHITELIST.add(Items.HAY_BLOCK);
        CLUTCH_WHITELIST.add(Items.SLIME_BLOCK);
        CLUTCH_WHITELIST.add(Items.HONEY_BLOCK);
        CLUTCH_WHITELIST.add(Items.SWEET_BERRIES);
        CLUTCH_WHITELIST.add(Items.TWISTING_VINES);
        CLUTCH_WHITELIST.add(Items.SCAFFOLDING);
    }

    public static BlockMlgHandler targetCentre() {
        return targetFixedPoint(0.5, 0.5);
    }

    public static BlockMlgHandler targetFixedPoint(double x, double z) {
        return new BlockMlgHandler() {
            @Override
            protected void adjustPos(PlayerEntity player) {
                BlockMlgHandler.targetFixedPoint(player, x, z);
            }
        };
    }

    public static void targetFixedPoint(PlayerEntity player, double x, double z) {
        Vec3d playerPos = player.getPos();
        if (playerPos.x < 0) {
            KeyboardHandler.directions[1] = Math.abs(playerPos.x) % 1 > x;
            KeyboardHandler.directions[3] = Math.abs(playerPos.x) % 1 < x;
        } else {
            KeyboardHandler.directions[1] = Math.abs(playerPos.x) % 1 < x;
            KeyboardHandler.directions[3] = Math.abs(playerPos.x) % 1 > x;
        }
        if (playerPos.z < 0) {
            KeyboardHandler.directions[0] = Math.abs(playerPos.z) % 1 < z;
            KeyboardHandler.directions[2] = Math.abs(playerPos.z) % 1 > z;
        } else {
            KeyboardHandler.directions[0] = Math.abs(playerPos.z) % 1 > z;
            KeyboardHandler.directions[2] = Math.abs(playerPos.z) % 1 < z;
        }
    }

    protected abstract void adjustPos(PlayerEntity player);

    public void handle(PlayerEntity player, Runnable useItem) {
        if (!player.isOnGround()) {
            ItemStack stackInHand = player.getStackInHand(player.getActiveHand());
            if (ControlledClient.doNextClutch) {
                if (stackInHand.isOf(Items.SLIME_BLOCK)) {
                    KeyboardHandler.space = true;
                } else if (stackInHand.isOf(Items.SCAFFOLDING)) {
                    KeyboardHandler.shift = true;
                }
            }
            if (canClutch(stackInHand) && MinecraftClient.getInstance().crosshairTarget instanceof BlockHitResult hitResult && ControlledClient.doNextClutch) {
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

    private static boolean canClutch(ItemStack stack) {
        if (CLUTCH_WHITELIST.contains(stack.getItem())) return true;
        return stack.isIn(ItemTags.BEDS);
    }
}
