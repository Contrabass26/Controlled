package com.contrabass.controlled;

import com.contrabass.controlled.script.Script;
import com.contrabass.controlled.script.ScriptRegisterCallback;
import com.contrabass.controlled.script.ShiftBridgeScript;
import com.contrabass.controlled.script.UpwardBridgeScript;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.world.World;

public class ControlledClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ScriptRegisterCallback.EVENT.register(consumer -> {
            consumer.accept(new ShiftBridgeScript());
            consumer.accept(new UpwardBridgeScript());
        });
        ControlledKeyBindings.init();
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ClientPlayerEntity player = client.player;
            World world = client.world;
            assert player != null;
            ControlledKeyBindings.handleKeyBindings();
            Script.tick(world, player);
        });
        Script.registerScripts();
    }
}
