package com.contrabass.controlled.script;

import com.contrabass.controlled.ControlledInit;
import com.contrabass.controlled.ControlledInputHandler;
import com.contrabass.controlled.util.MathUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class PathfindingScript extends Script {

    private boolean stop = false;
    private List<Step> steps = new ArrayList<>();
    private BlockPos start = null;
    private BlockPos end = null;
    private List<BlockPos> visited = new ArrayList<>();
    private boolean starting = true;

    private int end(World world, PlayerEntity player, int current) {
        stop("w");
        stop("sprint");
        stop = false;
        starting = true;
        return -1;
    }

    private static boolean isPath(BlockPos pos, World world, PlayerEntity player) {
        BlockPos up1 = pos.up();
        BlockPos up2 = up1.up();
        return world.isTopSolid(pos, player) && !world.getBlockState(up1).isSolidBlock(world, up1) && !world.getBlockState(up2).isSolidBlock(world, up2);
    }

    private void explore(BlockPos pos, List<Step> steps, World world, PlayerEntity player) { // -x, +z; -126, 89, -455
        visited.add(pos);
        if (pos.equals(end)) {
            this.steps = new ArrayList<>(steps);
            ControlledInit.LOGGER.info("Found better path!");
            return;
        }
        if (steps.size() > this.steps.size() && this.steps.size() != 0) {
//            ControlledInit.LOGGER.info("Rejected branch due to length");
            return;
        }
        BlockPos difference = end.subtract(pos);
        Direction xGood = difference.getX() == 0 ? Direction.EAST : Direction.fromVector(difference.getX() / Math.abs(difference.getX()), 0, 0);
        Direction zGood = difference.getZ() == 0 ? Direction.SOUTH : Direction.fromVector(0, 0, difference.getZ() / Math.abs(difference.getZ()));
        Direction xBad = xGood == null ? null : xGood.getOpposite();
        Direction zBad = zGood == null ? null : zGood.getOpposite();
        Direction[] directions = Math.abs(difference.getX()) > Math.abs(difference.getZ()) ? new Direction[]{xGood, zGood, zBad, xBad} : new Direction[]{zGood, xGood, xBad, zBad};
        List<Step> newSteps = new ArrayList<>(steps);
        newSteps.add(null);
        int last = steps.size();
        for (Direction direction : directions) {
            if (direction == null) {
                continue;
            }
            newSteps.set(last, new Step(direction, pos));
            BlockPos newPos = pos.offset(direction);
            if (!visited.contains(newPos) && isPath(pos, world, player)) {
                explore(newPos, newSteps, world, player);
            }
        }
    }

    @Override
    protected Task getTask(int index) {
        if (stop || ControlledInputHandler.pathfindingTarget == null) return this::end;
        return (world, player, current) -> {
            if (starting) {
                starting = false;
                // Initialise everything
                start("w");
                start("sprint");
                start = player.getBlockPos().down();
                end = ControlledInputHandler.pathfindingTarget;
                visited = new ArrayList<>();
                steps = new ArrayList<>();
                // Generate steps
                BlockPos pos = start.mutableCopy();
                explore(pos, new ArrayList<>(), world, player);
                if (steps.size() == 0) {
                    ControlledInit.LOGGER.info("No viable paths found for %s to %s".formatted(start, end));
                    return end(world, player, current);
                }
                ControlledInit.LOGGER.info(steps.toString());
            }
            Step step = steps.get(current);
            yaw(step.direction.asRotation());
            int proposed = steps.get(current).isComplete(player.getBlockPos().down()) ? current + 1 : current;
            return proposed >= steps.size() ? end(world, player, current) : proposed;
        };
    }

    @Override
    protected void prepareStop() {
        stop = true;
    }

    private record Step(Direction direction, BlockPos start) {

        public boolean isComplete(BlockPos current) {
            Vec3i directionVector = direction.getVector();
            int operativeStartComponent = MathUtils.dot(directionVector, start);
            int operativeEndComponent = MathUtils.dot(directionVector, current);
            return Math.abs(operativeEndComponent - operativeStartComponent) >= 0.8;
        }

        @Override
        public String toString() {
            return "%s -> %s".formatted(start, start.add(direction.getVector()));
        }
    }
}
