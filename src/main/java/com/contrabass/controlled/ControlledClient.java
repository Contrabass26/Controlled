package com.contrabass.controlled;

import com.contrabass.controlled.clutch_handler.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.network.ClientPlayerEntity;

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
        ControlledKeyBindings.init();
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ClientPlayerEntity player = client.player;
            assert player != null;
            ControlledKeyBindings.handleKeyBindings(player);
        });
    }
}
