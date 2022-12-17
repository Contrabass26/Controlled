package com.contrabass.controlled.clutch_handler;

import net.minecraft.entity.player.PlayerEntity;

public interface MlgHandler {

    void handle(PlayerEntity player, Runnable useItem);
}
