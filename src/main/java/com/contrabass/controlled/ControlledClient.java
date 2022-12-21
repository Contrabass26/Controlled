package com.contrabass.controlled;

import com.contrabass.controlled.clutch_handler.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.network.ClientPlayerEntity;

import java.util.ArrayList;
import java.util.List;

public class ControlledClient implements ClientModInitializer {

    public static final List<MlgHandler> MLG_HANDLERS;
    static {
        MLG_HANDLERS = new ArrayList<>();
        MLG_HANDLERS.add(new BoatMlgHandler());
        MLG_HANDLERS.add(new HayBlockMlgHandler());
        MLG_HANDLERS.add(new HoneyBlockMlgHandler());
        MLG_HANDLERS.add(new HoneyBlockSideMlgHandler());
        MLG_HANDLERS.add(new LadderMlgHandler());
        MLG_HANDLERS.add(new ScaffoldingMlgHandler());
        MLG_HANDLERS.add(new SlimeBlockMlgHandler());
        MLG_HANDLERS.add(new SweetBerriesMlgHandler());
        MLG_HANDLERS.add(new TwistingVinesMlgHandler());
        MLG_HANDLERS.add(new WaterMlgHandler());
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
