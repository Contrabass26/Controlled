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

    public static final List<ClutchHandler> CLUTCH_HANDLERS;
    static {
        CLUTCH_HANDLERS = new ArrayList<>();
        CLUTCH_HANDLERS.add(new BoatClutchHandler());
        CLUTCH_HANDLERS.add(new HayBlockClutchHandler());
        CLUTCH_HANDLERS.add(new HoneyBlockClutchHandler());
        CLUTCH_HANDLERS.add(new HoneyBlockSideClutchHandler());
        CLUTCH_HANDLERS.add(new LadderClutchHandler());
        CLUTCH_HANDLERS.add(new ScaffoldingClutchHandler());
        CLUTCH_HANDLERS.add(new SlimeBlockClutchHandler());
        CLUTCH_HANDLERS.add(new SweetBerriesClutchHandler());
        CLUTCH_HANDLERS.add(new TwistingVinesClutchHandler());
        CLUTCH_HANDLERS.add(new WaterClutchHandler());
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
            ControlledKeyBindings.handleKeyBindings();
            Script.tick(world, player);
        });
        Script.registerScripts();
    }
}
