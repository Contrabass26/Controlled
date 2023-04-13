package com.contrabass.controlled;

import com.contrabass.controlled.handler.*;
import com.contrabass.controlled.script.Script;
import com.contrabass.controlled.script.ScriptRegisterCallback;
import com.contrabass.controlled.script.ShiftBridgeScript;
import com.contrabass.controlled.script.UpwardBridgeScript;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class ControlledClient implements ClientModInitializer {

    public static final List<ClutchHandler> MLG_HANDLERS;
    static {
        MLG_HANDLERS = new ArrayList<>();
        MLG_HANDLERS.add(new BoatClutchHandler());
        MLG_HANDLERS.add(new HayBlockClutchHandler());
        MLG_HANDLERS.add(new HoneyBlockClutchHandler());
        MLG_HANDLERS.add(new HoneyBlockSideClutchHandler());
        MLG_HANDLERS.add(new LadderClutchHandler());
        MLG_HANDLERS.add(new ScaffoldingClutchHandler());
        MLG_HANDLERS.add(new SlimeBlockClutchHandler());
        MLG_HANDLERS.add(new SweetBerriesClutchHandler());
        MLG_HANDLERS.add(new TwistingVinesClutchHandler());
        MLG_HANDLERS.add(new WaterClutchHandler());
    }

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
            ControlledKeyBindings.handleKeyBindings(player);
            Script.tick(world, player);
        });
        Script.registerScripts();
    }
}
