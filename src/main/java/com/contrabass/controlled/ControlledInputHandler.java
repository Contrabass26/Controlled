package com.contrabass.controlled;

import com.contrabass.controlled.handler.ClutchHandler;
import com.contrabass.controlled.util.MathUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector2d;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

public class ControlledInputHandler {

    private static final List<InputModifier.Movement> MOVEMENT_INPUT_MODIFIERS = new ArrayList<>();
    private static final List<InputModifier.Key> KEY_INPUT_MODIFIERS = new ArrayList<>();
    private static final Random RANDOM = new Random();

    public static Vector2d target = null;
    public static Boolean shift = null;
    public static Boolean jump = null;
    public static int doNextRightClick = 0;
    public static boolean fastRightClick = false;
    public static boolean fastLeftClick = false;
    public static int doNextLeftClick = 0;
    public static Integer switchToSlot = null;
    public static Float moveToYaw = null;
    public static Float moveToPitch = null;
    public static int fastClickCounter = 1;
    public static float fastClickThreshold = 1f;
    public static float fastClickShakeIntensity = 0f;
    public static float fastClickShakeChance = 1f;
    public static boolean pathfind = false;
    public static BlockPos pathfindingTarget = null;

    private ControlledInputHandler() {}

    private static void reset() {
        target = null;
        jump = null;
        shift = null;
    }
    
    public static void handleKeys(Input keyboardInput, boolean slowDown, float factor) {
        // WASD
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        assert player != null;
        if (target != null) {
            Vector2d current = MathUtils.flatten(player.getPos());
            boolean[] keys = MathUtils.getKeysFor(current, target, player.getYaw());
            keyboardInput.pressingForward = keys[0];
            keyboardInput.pressingRight = keys[1];
            keyboardInput.pressingBack = keys[2];
            keyboardInput.pressingLeft = keys[3];
        }
        // Shift and jump fields
        if (shift != null) keyboardInput.sneaking = shift;
        if (jump != null) keyboardInput.jumping = jump;
        // Input modifiers
        MOVEMENT_INPUT_MODIFIERS.sort(InputModifier::compareTo);
        MOVEMENT_INPUT_MODIFIERS.forEach(m -> m.accept(keyboardInput));
        // Related calculations
        keyboardInput.movementForward = getMovementMultiplier(keyboardInput.pressingForward, keyboardInput.pressingBack);
        keyboardInput.movementSideways = getMovementMultiplier(keyboardInput.pressingLeft, keyboardInput.pressingRight);
        if (slowDown) {
            keyboardInput.movementSideways *= factor;
            keyboardInput.movementForward *= factor;
        }
        reset();
    }

    public static void handleInputEvents(Runnable itemUse, Runnable attack, PlayerEntity player, MinecraftClient client, GameOptions options) {
        for (ClutchHandler handler : ControlledClient.CLUTCH_HANDLERS) {
            handler.handle(player, itemUse);
        }
        if (fastRightClick) {
            float diff = fastClickThreshold - fastClickCounter;
            if (diff < 0 || RANDOM.nextFloat() > diff) {
                fastClickCounter = 0;
                itemUse.run();
            }
            fastClickCounter++;
        } else {
            fastClickCounter = (int) fastClickThreshold;
        }
        if (doNextRightClick > 0) {
            itemUse.run();
            doNextRightClick = 0;
        }
        if (fastLeftClick) {
            float diff = fastClickThreshold - fastClickCounter;
            if (diff < 0 || RANDOM.nextFloat() > diff) {
                fastClickCounter = 0;
                attack.run();
            }
            fastClickCounter++;
        } else {
            fastClickCounter = (int) fastClickThreshold;
        }
        if (doNextLeftClick > 0) {
            attack.run();
            doNextLeftClick = 0;
        }
        if (moveToYaw != null) {
            player.setYaw(moveToYaw);
            moveToYaw = null;
        }
        if (moveToPitch != null) {
            player.setPitch(moveToPitch);
            moveToPitch = null;
        }
        // Slot switching
        if (switchToSlot != null) {
            if (client.currentScreen == null) {
                player.getInventory().selectedSlot = switchToSlot;
            }
            switchToSlot = null;
        }
        // Shaking for fast click
        if ((fastLeftClick || fastRightClick) && fastClickShakeIntensity != 0) {
            if (RANDOM.nextFloat() < fastClickShakeChance) {
                float yaw = player.getYaw();
                float pitch = player.getPitch();
                yaw += RANDOM.nextFloat(fastClickShakeIntensity * 2) - fastClickShakeIntensity;
                pitch += RANDOM.nextFloat(fastClickShakeIntensity * 2) - fastClickShakeIntensity;
                player.setYaw(yaw);
                player.setPitch(pitch);
            }
        }
        // Input modifiers
        KEY_INPUT_MODIFIERS.sort(InputModifier::compareTo);
        KEY_INPUT_MODIFIERS.forEach(m -> m.accept(options));
    }

    private static float getMovementMultiplier(boolean positive, boolean negative) {
        if (positive == negative) {
            return 0.0F;
        } else {
            return positive ? 1.0F : -1.0F;
        }
    }

    public static void addInputModifier(InputModifier<?> modifier) {
        if (modifier instanceof InputModifier.Movement movementModifier) {
            MOVEMENT_INPUT_MODIFIERS.removeIf(m -> m.equals(movementModifier));
            MOVEMENT_INPUT_MODIFIERS.add(movementModifier);
        } else if (modifier instanceof InputModifier.Key keyModifier) {
            KEY_INPUT_MODIFIERS.removeIf(m -> m.equals(keyModifier));
            KEY_INPUT_MODIFIERS.add(keyModifier);
        } else {
            throw new IllegalArgumentException("Expected InputModifier.Key or InputModifier.Movement, got " + modifier.getClass());
        }
    }

    public static void removeInputModifier(Predicate<String> idPredicate) {
        MOVEMENT_INPUT_MODIFIERS.removeIf(m -> idPredicate.test(m.id));
        KEY_INPUT_MODIFIERS.removeIf(m -> idPredicate.test(m.id));
    }

    public static void lockRotation() {
        PlayerEntity player = MinecraftClient.getInstance().player;
        assert player != null;
        float yaw = player.getYaw();
        float newYaw = Math.round(yaw / 45f) * 45f;
        player.setYaw(newYaw);
    }

    public static void setPathfindingTarget() {
        PlayerEntity player = MinecraftClient.getInstance().player;
        assert player != null;
        World world = player.getWorld();
        Vec3d direction = player.getRotationVector().normalize();
        Vec3d pos = player.getEyePos();
        for (int i = 0; i < 50; i++) {
            pos = pos.add(direction);
            BlockPos blockPos = new BlockPos((int) Math.floor(pos.x), (int) Math.floor(pos.y), (int) Math.floor(pos.z));
            if (world.getBlockState(blockPos).isSolidBlock(world, blockPos)) {
                ControlledInputHandler.pathfindingTarget = blockPos;
                break;
            }
        }
    }
}
