package com.contrabass.controlled.mixin;

import com.contrabass.controlled.clutch_handler.BlockMlgHandler;
import com.contrabass.controlled.clutch_handler.BoatMlgHandler;
import com.contrabass.controlled.clutch_handler.MlgHandler;
import com.contrabass.controlled.clutch_handler.WaterMlgHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Set;

@Mixin(MinecraftClient.class)
public abstract class InputEventMixin {

    private static final Set<MlgHandler> MLG_HANDLERS;
    static {
        MLG_HANDLERS = new HashSet<>();
        MLG_HANDLERS.add(new WaterMlgHandler());
        MLG_HANDLERS.add(new BoatMlgHandler());
        MLG_HANDLERS.add(BlockMlgHandler.targetCentre());
    }

    @Shadow @Nullable public ClientPlayerEntity player;

    @Shadow protected abstract void doItemUse();

    @Inject(method = "handleInputEvents()V", at = @At("TAIL"))
    public void handleInputEvents(CallbackInfo callback) {
        assert player != null;
        for (MlgHandler handler : MLG_HANDLERS) {
            handler.handle(player, this::doItemUse);
        }
    }
}
