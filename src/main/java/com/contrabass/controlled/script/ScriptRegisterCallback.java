package com.contrabass.controlled.script;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import java.util.function.Consumer;

public interface ScriptRegisterCallback {

    Event<ScriptRegisterCallback> EVENT = EventFactory.createArrayBacked(ScriptRegisterCallback.class,
            (listeners) -> (consumer) -> {
                for (ScriptRegisterCallback listener : listeners) {
                    listener.fire(consumer);
                }
            });

    void fire(Consumer<CodeScript> consumer);
}
