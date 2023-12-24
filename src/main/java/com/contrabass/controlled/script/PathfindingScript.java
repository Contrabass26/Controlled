package com.contrabass.controlled.script;

import com.contrabass.controlled.ControlledInit;
import com.contrabass.controlled.ControlledInputHandler;
import com.contrabass.controlled.util.MathUtils;
import com.google.common.collect.Comparators;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class PathfindingScript extends Script {

    private boolean stop = false;
    private Path path = new Path();
    private BlockPos start = null;
    private BlockPos end = null;
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

    private void explore(BlockPos pos, Path path, World world, PlayerEntity player, Set<BlockPos> visited) {
        visited.add(pos);
        if (pos.equals(end)) {
            this.path = new Path(comparePaths(this.path, path));
            ControlledInit.LOGGER.info("Found path!");
            return;
        }
        if (path.length() > 25) return;
        if (path.length() > this.path.length() && this.path.length() != 0) {
//            ControlledInit.LOGGER.info("Rejected branch due to length");
            return;
        }
        BlockPos difference = end.subtract(pos);
        Direction xGood = difference.getX() == 0 ? Direction.EAST : Direction.fromVector(difference.getX() / Math.abs(difference.getX()), 0, 0);
        Direction zGood = difference.getZ() == 0 ? Direction.SOUTH : Direction.fromVector(0, 0, difference.getZ() / Math.abs(difference.getZ()));
        Direction xBad = xGood == null ? null : xGood.getOpposite();
        Direction zBad = zGood == null ? null : zGood.getOpposite();
        Direction[] directions = Math.abs(difference.getX()) > Math.abs(difference.getZ()) ? new Direction[]{xGood, zGood, zBad, xBad} : new Direction[]{zGood, xGood, xBad, zBad};
        Path newSteps = new Path(path);
        newSteps.add(null);
        int last = path.length();
        for (Direction direction : directions) {
            if (direction == null) {
                continue;
            }
            newSteps.set(last, new Step(direction, pos));
            BlockPos newPos = pos.offset(direction);
            if (!visited.contains(newPos) && isPath(pos, world, player)) {
                explore(newPos, newSteps, world, player, new HashSet<>(visited));
                if (this.path.length() != 0) {
                    break;
                }
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
                path = new Path();
                // Generate steps
                BlockPos pos = start.mutableCopy();
                explore(pos, new Path(), world, player, new HashSet<>());
                if (path.length() == 0) {
                    ControlledInit.LOGGER.info("No viable paths found for %s to %s".formatted(start, end));
                    return end(world, player, current);
                }
                ControlledInit.LOGGER.info(path.toString());
            }
            Step step = path.get(current);
            yaw(step.direction.asRotation());
            int proposed = path.get(current).isComplete(player.getBlockPos().down()) ? current + 1 : current;
            return proposed >= path.length() ? end(world, player, current) : proposed;
        };
    }

    @Override
    protected void prepareStop() {
        stop = true;
    }

    private static Path comparePaths(Path p1, Path p2) {
        if (p1.length() == 0) return p2;
        Comparator<Path> sizeComparator = Comparator.comparing(Path::length);
        return Comparators.min(p1, p2, sizeComparator.thenComparing(PathfindingScript::countPathTurns));
    }

    private static int countPathTurns(Path path) {
        int turns = 0;
        Direction last = null;
        for (Step step : path) {
            if (step.direction != last) {
                turns++;
            }
            last = step.direction;
        }
        return turns - 1;
    }

    private record Step(Direction direction, BlockPos start) {

        public boolean isComplete(BlockPos current) {
            Vec3i directionVector = direction.getVector();
            int operativeStartComponent = MathUtils.dot(directionVector, start);
            int operativeEndComponent = MathUtils.dot(directionVector, current);
            return Math.abs(operativeEndComponent - operativeStartComponent) >= 0.8;
        }

        public Step copy() {
            return new Step(direction, start);
        }

        @Override
        public String toString() {
            return start.offset(direction).toString();
        }
    }

    private static class Path implements Iterable<Step> {

        private final List<Step> steps;

        public Path() {
            this.steps = new ArrayList<>();
        }

        public Path(Path other) {
            this();
            for (Step step : other.steps) {
                this.steps.add(step.copy());
            }
        }

        @Override
        public String toString() {
            return steps.toString();
        }

        public void add(Step step) {
            steps.add(step);
        }

        public int length() {
            return steps.size();
        }

        public void set(int index, Step step) {
            steps.set(index, step);
        }

        public Step get(int index) {
            return steps.get(index);
        }

        @NotNull
        @Override
        public Iterator<Step> iterator() {
            return new Iterator<>() {
                private int next = 0;

                @Override
                public boolean hasNext() {
                    return next < steps.size();
                }

                @Override
                public Step next() {
                    next++;
                    return steps.get(next - 1);
                }
            };
        }
    }
}
