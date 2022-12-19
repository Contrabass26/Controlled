package com.contrabass.controlled.mixin;

import com.contrabass.controlled.ControlledClient;
import com.contrabass.controlled.clutch_handler.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
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
        MLG_HANDLERS.add(new LadderMlgHandler());
    }

    @Shadow @Nullable public ClientPlayerEntity player;

    @Shadow protected abstract void doItemUse();

    @Shadow protected abstract boolean doAttack();

    @Shadow @Nullable public Screen currentScreen;

    @Inject(method = "handleInputEvents()V", at = @At("TAIL"))
    public void handleInputEvents(CallbackInfo callback) {
        assert player != null;
        for (MlgHandler handler : MLG_HANDLERS) {
            handler.handle(player, this::doItemUse);
        }
        if (ControlledClient.doNextRightClick) {
            doItemUse();
            ControlledClient.doNextRightClick = false;
        }
        if (ControlledClient.doNextLeftClick) {
            this.doAttack();
            ControlledClient.doNextLeftClick = false;
        }
        // Slot switching
        if (ControlledClient.switchToSlot != null) {
            if (!this.player.isCreative() && this.currentScreen == null) {
                this.player.getInventory().selectedSlot = ControlledClient.switchToSlot;
            } else {
                CreativeInventoryScreen.onHotbarKeyPress((MinecraftClient) ((Object) this), ControlledClient.switchToSlot, false, false);
            }
            ControlledClient.switchToSlot = null;
        }
    }
}
