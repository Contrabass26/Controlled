package com.contrabass.controlled.handler;

import com.contrabass.controlled.ControlledClient;
import com.contrabass.controlled.ControlledInputHandler;
import com.contrabass.controlled.script.Script;
import com.contrabass.controlled.util.MathUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Pair;
import net.minecraft.world.World;
import org.joml.Vector2d;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class ClutchHandler {

    private static boolean doNextClutch = false;

    public abstract void handle(PlayerEntity player, Runnable useItem);

    public abstract int getScore(World world, PlayerEntity player, List<ItemStack> hotbar);

    public abstract int getSlotToUse(PlayerEntity player, List<ItemStack> hotbar);

    protected void clearCaches() {}

    public static void doNextClutch() {
        doNextClutch = true;
        Script.stopAllExcept(null);
    }

    public static boolean willClutchNext() {
        return doNextClutch;
    }

    protected static void finishClutch() {
        doNextClutch = false;
        ControlledClient.CLUTCH_HANDLERS.forEach(ClutchHandler::clearCaches);
    }

    public static void switchToBestSlot(PlayerEntity player) {
        List<ItemStack> hotbar = getHotbar(player);
        Pair<ClutchHandler, Integer> best = new Pair<>(null, 0);
        for (ClutchHandler handler : ControlledClient.CLUTCH_HANDLERS) {
            int score = handler.getScore(player.getWorld(), player, hotbar);
            if (score > best.getRight()) {
                best = new Pair<>(handler, score);
            }
        }
        if (best.getRight() != 0) {
            ControlledInputHandler.switchToSlot = best.getLeft().getSlotToUse(player, hotbar);
        }
    }

    protected static Vector2d targetCentre(PlayerEntity player) {
        return new Vector2d(
                MathUtils.addPlusMinus(MathUtils.roundToZero(player.getX(), 1), 0.5),
                MathUtils.addPlusMinus(MathUtils.roundToZero(player.getZ(), 1), 0.5)
        );
    }

    protected static int findSlotFor(List<ItemStack> hotbar, Item item) {
        for (int i = 0; i < hotbar.size(); i++) {
            if (hotbar.get(i).isOf(item)) {
                return i;
            }
        }
        return -1;
    }

    protected static int findSlotFor(List<ItemStack> hotbar, TagKey<Item> tag) {
        for (int i = 0; i < hotbar.size(); i++) {
            if (hotbar.get(i).isIn(tag)) {
                return i;
            }
        }
        return -1;
    }

    public static List<ItemStack> getHotbar(PlayerEntity player) {
        return IntStream.range(0, 9).mapToObj(player.getInventory()::getStack).collect(Collectors.toList());
    }
}
