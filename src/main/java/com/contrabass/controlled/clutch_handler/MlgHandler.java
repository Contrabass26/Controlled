package com.contrabass.controlled.clutch_handler;

import com.contrabass.controlled.ControlledClient;
import com.contrabass.controlled.InputHandler;
import com.contrabass.controlled.MathUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.joml.Vector2d;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class MlgHandler {

    private static boolean doNextClutch = false;

    public abstract void handle(PlayerEntity player, Runnable useItem);

    public abstract int getScore(World world, PlayerEntity player, List<ItemStack> hotbar);

    public abstract int getSlotToUse(PlayerEntity player, List<ItemStack> hotbar);

    protected void clearCaches() {}

    public static void doNextClutch() {
        doNextClutch = true;
    }

    public static boolean willClutchNext() {
        return doNextClutch;
    }

    protected static void finishClutch() {
        doNextClutch = false;
        ControlledClient.MLG_HANDLERS.forEach(MlgHandler::clearCaches);
    }

    public static void switchToBestSlot(PlayerEntity player) {
        List<ItemStack> hotbar = getHotbar(player);
        Pair<MlgHandler, Integer> best = new Pair<>(null, 0);
        for (MlgHandler handler : ControlledClient.MLG_HANDLERS) {
            int score = handler.getScore(player.world, player, hotbar);
            if (score > best.getRight()) {
                best = new Pair<>(handler, score);
            }
        }
        if (best.getRight() != 0) {
            InputHandler.switchToSlot = best.getLeft().getSlotToUse(player, hotbar);
        }
    }

    protected static boolean isTargetingBlock(MinecraftClient minecraft) {
        if (minecraft.crosshairTarget instanceof BlockHitResult blockHitResult) {
            return blockHitResult.getType() != HitResult.Type.MISS;
        }
        return false;
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

    protected static int getTopBlock(World world, BlockPos start) {
        while (!world.getBlockState(start).isSolidBlock(world, start) && start.getY() != -64) {
            start = start.down();
        }
        return start.getY();
    }

    protected static BlockPos getTopBlockPos(World world, BlockPos start) {
        return new BlockPos(start.getX(), getTopBlock(world, start), start.getZ());
    }

    public static List<ItemStack> getHotbar(PlayerEntity player) {
        return IntStream.range(0, 9).mapToObj(player.getInventory()::getStack).collect(Collectors.toList());
    }
}
