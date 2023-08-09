package com.contrabass.controlled.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseHandlerMixin {

    @Unique
    private double horizontalScroll;

    @Redirect(method="onMouseButton", at=@At(value="FIELD", target="Lnet/minecraft/client/MinecraftClient;IS_SYSTEM_MAC:Z"))
    private boolean onMouseButton() {
        return false;
    }

    @Inject(method="onMouseScroll", at=@At(value="HEAD"))
    private void onMouseScroll(long window, double horizontal, double vertical, CallbackInfo callback) {
        this.horizontalScroll = horizontal;
    }

    @ModifyVariable(method="onMouseScroll", ordinal=1, at=@At(value="LOAD"), argsOnly = true)
    private double onMouseScroll_horizontal(double verticalScroll) {
        if (MinecraftClient.IS_SYSTEM_MAC && verticalScroll == 0) {
            return horizontalScroll;
        }
        return verticalScroll;
    }
}
