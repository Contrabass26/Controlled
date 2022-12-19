package com.contrabass.controlled.clutch_handler;

import com.contrabass.controlled.ControlledClient;
import com.contrabass.controlled.KeyboardHandler;
import com.contrabass.controlled.MathUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import org.joml.Vector2d;

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
        return new BlockMlgHandler() {
            @Override
            protected void adjustPos(PlayerEntity player) {
                targetCentre(player);
            }
        };
    }

    public static void targetCentre(PlayerEntity player) {
        KeyboardHandler.target = new Vector2d(
                MathUtils.addPlusMinus(MathUtils.roundToZero(player.getX(), 1), 0.5),
                MathUtils.addPlusMinus(MathUtils.roundToZero(player.getZ(), 1), 0.5)
        );
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
