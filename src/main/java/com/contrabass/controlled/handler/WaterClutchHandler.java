package com.contrabass.controlled.handler;

import com.contrabass.controlled.ControlledInit;
import com.contrabass.controlled.ControlledInputHandler;
import com.contrabass.controlled.config.Configs;
import com.contrabass.controlled.script.Script;
import com.contrabass.controlled.util.ControlledUtils;
import com.contrabass.controlled.util.MathUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.joml.Vector2d;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class WaterClutchHandler {

    private static boolean doNextClutch = false;
    private static boolean justPlaced = false;
    public static BlockPos target = null;

    private WaterClutchHandler() {}

    public static void doNextClutch() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        assert player != null;
        World world = player.world;
        if (world.getDimension().ultrawarm()) return; // Nether-like dimensions - water won't work
        List<ItemStack> hotbar = IntStream.range(0, 9).mapToObj(player.getInventory()::getStack).collect(Collectors.toList());
        int slotToUse = getSlotToUse(hotbar);
        if (slotToUse == -1) return; // No water bucket in hotbar
        ControlledInputHandler.switchToSlot = slotToUse;
        doNextClutch = true;
        Script.stopAllExcept(null);
        // Do targeting
//        double a = 22.19;
        double a = 30;
        BlockPos topBlockPos = ControlledUtils.getTopBlockPos(world, player.getBlockPos());
        double s = player.getY() - topBlockPos.getY();
        double t = Math.sqrt(2d * s / a) * 20;
        Vec3d projected = player.getPos().add(player.getVelocity().multiply(t));
        target = new BlockPos(projected.x, topBlockPos.getY(), projected.z);
        ControlledInputHandler.target = new Vector2d(
                MathUtils.addPlusMinus(MathUtils.roundToZero(projected.x, 1), 0.5),
                MathUtils.addPlusMinus(MathUtils.roundToZero(projected.z, 1), 0.5)
        );
    }

    public static boolean willClutchNext() {
        return doNextClutch;
    }

    protected static void finishClutch() {
        doNextClutch = false;
        ControlledInputHandler.target = null;
    }

    protected static Vector2d targetCentre(PlayerEntity player) {
        return new Vector2d(
                MathUtils.addPlusMinus(MathUtils.roundToZero(player.getX(), 1), 0.5),
                MathUtils.addPlusMinus(MathUtils.roundToZero(player.getZ(), 1), 0.5)
        );
    }

    public static void handle(PlayerEntity player, Runnable useItem) {
        if (!player.isOnGround()) {
            if (player.getStackInHand(player.getActiveHand()).getItem() == Items.WATER_BUCKET && WaterClutchHandler.willClutchNext()) {
                if (ControlledUtils.isTargetingBlock(MinecraftClient.getInstance())) {
                    useItem.run();
                    WaterClutchHandler.finishClutch();
                    justPlaced = true;
                } else if (Configs.Generic.ADJUST_CLUTCH_POSITION.getBooleanValue()) {
//                    ControlledInputHandler.target = targetCentre(player);
                }
            }
        } else if (justPlaced) {
            useItem.run();
            justPlaced = false;
        }
    }

    public static int getSlotToUse(List<ItemStack> hotbar) {
        for (int i = 0; i < hotbar.size(); i++) {
            if (hotbar.get(i).isOf(Items.WATER_BUCKET)) {
                return i;
            }
        }
        return -1;
    }
}
