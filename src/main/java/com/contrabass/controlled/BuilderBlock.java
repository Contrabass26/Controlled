package com.contrabass.controlled;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class BuilderBlock extends Block {

    private static final Set<FutureBreak> FUTURE_BREAKS = new HashSet<>();

    public BuilderBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        if (world instanceof ServerWorld serverWorld) {
            FUTURE_BREAKS.add(new FutureBreak(serverWorld, pos));
        }
        if (placer instanceof PlayerEntity player && !player.isCreative()) {
            itemStack.setCount(65);
        }
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);
        if (world instanceof ServerWorld serverWorld) {
            FUTURE_BREAKS.removeIf((new FutureBreak(serverWorld, pos))::equals);
        }
    }

    public static void onServerTick() {
        FUTURE_BREAKS.removeAll(FUTURE_BREAKS.stream().filter(FutureBreak::tick).collect(Collectors.toSet()));
    }

    private static final class FutureBreak {

        private static final int START_TIME = 100;

        private final ServerWorld world;
        private final BlockPos pos;
        private int time = START_TIME;

        private FutureBreak(ServerWorld world, BlockPos pos) {
            this.world = world;
            this.pos = pos;
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) return true;
            if (other instanceof BuilderBlock.FutureBreak futureBreak) {
                return world == futureBreak.world && pos.equals(futureBreak.pos);
            }
            return false;
        }

        private boolean tick() {
            time--;
            if (time == 0) {
                world.setBlockState(pos, Blocks.AIR.getDefaultState());
                return true;
            }
            return false;
        }
    }
}
