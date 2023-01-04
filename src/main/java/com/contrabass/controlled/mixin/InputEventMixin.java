package com.contrabass.controlled.mixin;

import com.contrabass.controlled.ControlledInputHandler;
import com.contrabass.controlled.handler.ShiftBridgeHandler;
import com.contrabass.controlled.handler.UpwardShiftBridgeHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class InputEventMixin {

    @Shadow @Nullable public ClientPlayerEntity player;

    @Shadow protected abstract void doItemUse();

    @Shadow protected abstract boolean doAttack();

    @Shadow @Final public GameOptions options;

    @Inject(method = "handleInputEvents()V", at = @At("TAIL"))
    public void handleInputEvents(CallbackInfo callback) {
        assert player != null;
        ShiftBridgeHandler.tick(player);
        UpwardShiftBridgeHandler.tick(player);
        ControlledInputHandler.handleInputEvents(this::doItemUse, this::doAttack, player, (MinecraftClient) ((Object) this), options);
    }
}
