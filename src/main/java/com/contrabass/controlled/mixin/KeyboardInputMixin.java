package com.contrabass.controlled.mixin;

import com.contrabass.controlled.KeyboardHandler;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public class KeyboardInputMixin extends Input {

    @Inject(method = "tick(ZF)V", at = @At("TAIL"))
    public void tick(boolean slowDown, float factor, CallbackInfo callback) {
        KeyboardHandler.handle(this, slowDown, factor);
    }
}
