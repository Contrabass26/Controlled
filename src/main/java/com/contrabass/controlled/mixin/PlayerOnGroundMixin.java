package com.contrabass.controlled.mixin;

import com.contrabass.controlled.script.Script;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class PlayerOnGroundMixin {

    @Inject(method = "setOnGround(Z)V", at = @At("HEAD"))
    public void setOnGround(boolean onGround, CallbackInfo callback) {
        Script.handleTrigger(Script.Trigger.PLAYER_ON_GROUND);
    }
}
