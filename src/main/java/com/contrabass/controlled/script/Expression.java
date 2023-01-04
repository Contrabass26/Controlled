package com.contrabass.controlled.script;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Direction;

import java.util.function.Function;

public interface Expression extends Function<PlayerEntity, String> {

    /**
     * @return [INDIRECT] A direction in String form
     */
    static Expression backwards() {
        return player -> Direction.fromRotation(player.getYaw() - 45).getOpposite().asString().toUpperCase();
    }
}
