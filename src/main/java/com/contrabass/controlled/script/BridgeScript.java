package com.contrabass.controlled.script;

import com.contrabass.controlled.util.ControlledUtils;

public class BridgeScript extends CodeScript {

    private boolean breakout = false;

    private final Task START = (world, player, current) -> {
        pitch(78);
        lockRotation();
        start("s");
        start("d");
        return current + 1;
    };

    private final Task LOOP = (world, player, current) -> {
        double distance = ControlledUtils.getDistanceBackwards(player);
        if (distance > 0.25 && distance < 0.9) {
            stop("shift");
        } else {
            start("shift");
        }
        use();
        return current + (breakout ? 1 : 0);
    };

    private final Task END = (world, player, current) -> {
        stop("shift");
        stop("s");
        stop("d");
        breakout = false;
        return -1;
    };

    public BridgeScript() {
        super();
    }

    @Override
    protected Task getTask(int index) {
        return switch (index) {
            case 0 -> START;
            case 1 -> LOOP;
            case 2 -> END;
            default -> null;
        };
    }

    @Override
    protected void prepareStop() {
        breakout = true;
    }
}
