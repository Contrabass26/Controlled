package com.contrabass.controlled.script;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Direction;

import java.util.function.Function;

public interface Expression extends Function<PlayerEntity, String> {

    /**
     * @return [INDIRECT] A direction in String form
     */
    static Expression backwardsDiagonal() {
        return player -> directionToString(Direction.fromRotation(player.getYaw() - 45).getOpposite());
    }

    static Expression backwards() {
        return player -> directionToString(Direction.fromRotation(player.getYaw()).getOpposite());
    }

    static Expression left() {
        return player -> directionToString(Direction.fromRotation(player.getYaw() - 90));
    }

    static Expression right() {
        return player -> directionToString(Direction.fromRotation(player.getYaw() + 90));
    }

    static Expression forward() {
        return player -> directionToString(Direction.fromRotation(player.getYaw()));
    }

    private static String directionToString(Direction direction) {
        return direction.asString().toUpperCase();
    }
}
