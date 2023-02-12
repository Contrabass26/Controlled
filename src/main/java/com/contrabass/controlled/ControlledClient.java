package com.contrabass.controlled;

import com.contrabass.controlled.handler.*;
import com.contrabass.controlled.script.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
            consumer.accept(new BridgeScript());
            consumer.accept(new UpwardBridgeScript());
        });
        ControlledKeyBindings.init();
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return new Identifier(ControlledInit.MOD_ID, "scripts");
            }

            @Override
            public void reload(ResourceManager manager) {
                Script.clearScripts();
                Map<Identifier, Resource> scripts = manager.findResources("script", i -> i.getPath().endsWith(".msc"));
                for (Identifier identifier : scripts.keySet()) {
                    try {
                        Script.register(identifier, manager);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ClientPlayerEntity player = client.player;
            World world = client.world;
            assert player != null;
            ControlledKeyBindings.handleKeyBindings(player);
            Script.tick();
            CodeScript.tick(world, player);
        });
        CodeScript.registerScripts();
    }
}
