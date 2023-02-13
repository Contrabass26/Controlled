package com.contrabass.controlled.script;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;

public class UpwardBridgeScript extends Script {

    private int counter = 0;
    private boolean breakout = false;

    private final Task START = (world, player, current) -> {
        pitch(78);
        lockRotation();
        start("s");
        start("d");
        start("shift");
        return current + 1;
    };

    private final Task LOOP_1 = (world, player, current) -> {
        if (breakout) {
            breakout = false;
            return 4;
        }
        Direction side = Direction.fromRotation(player.getYaw() - 45).getOpposite();
        if (MinecraftClient.getInstance().crosshairTarget instanceof BlockHitResult hitResult && hitResult.getSide() == side) {
            stop("shift");
            use();
            start("jump");
            return current + 1;
        }
        return current;
    };

    private final Task BUFFER = (world, player, current) -> {
        counter++;
        if (counter == 3) {
            counter = 0;
            return current + 1;
        }
        return current;
    };

    private final Task LOOP_2 = (world, player, current) -> {
        use();
        stop("jump");
        start("shift");
        return 1;
    };

    private final Task END = (world, player, current) -> {
        stop("s");
        stop("d");
        stop("jump");
        stop("shift");
        return -1;
    };

    @Override
    protected Task getTask(int index) {
        return switch (index) {
            case 0 -> START;
            case 1 -> LOOP_1;
            case 2 -> BUFFER;
            case 3 -> LOOP_2;
            case 4 -> END;
            default -> null;
        };
    }

    @Override
    protected void prepareStop() {
        breakout = true;
    }
}
