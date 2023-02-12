package com.contrabass.controlled.script;

import com.contrabass.controlled.ControlledInputHandler;
import com.contrabass.controlled.InputModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public abstract class CodeScript {

    private static final Map<String, CodeScript> scripts = new HashMap<>();
    private static boolean registryFrozen = false;

    private final String modifierId;
    private int next = 0;
    private boolean running = false;
    private boolean acceptedKeyPress = false;

    protected CodeScript() {
        modifierId = this.getClass().getSimpleName();
    }

    // ABSTRACT METHODS //

    protected abstract Task getTask(int index);

    protected abstract void prepareStop();

    // PUBLIC METHODS //

    public void handleKeybind(boolean pressed) {
        if (!pressed && acceptedKeyPress) {
            acceptedKeyPress = false;
        } else if (pressed && !acceptedKeyPress) {
            if (running) prepareStop();
            else startScript();
            acceptedKeyPress = true;
        }
    }

    public static void registerScripts() {
        if (!registryFrozen) {
            ScriptRegisterCallback.EVENT.invoker().fire(script -> scripts.put(script.getClass().getSimpleName(), script));
            registryFrozen = true;
        }
    }

    public static CodeScript get(String name) {
        return scripts.get(name);
    }

    public static void tick(World world, PlayerEntity player) {
        scripts.values().forEach(s -> {
            if (s.running) {
                s.step(world, player);
            }
        });
    }

    // CHILD UTILITY METHODS //

    protected void pitch(float pitch) {
        ControlledInputHandler.moveToPitch = pitch;
    }

    protected void yaw(float yaw) {
        ControlledInputHandler.moveToYaw = yaw;
    }

    protected void lockRotation() {
        ControlledInputHandler.lockRotation();
    }

    protected void use() {
        ControlledInputHandler.doNextRightClick = 1;
    }

    protected void start(String key) {
        try {
            ControlledInputHandler.addInputModifier(createModifier(key, true));
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    protected void stop(String key) {
        try {
            ControlledInputHandler.addInputModifier(createModifier(key, false));
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    // INTERNAL METHODS //

    private void step(World world, PlayerEntity player) {
        next = getTask(next).run(world, player, next);
        if (next == -1) {
            next = 0;
            running = false;
            ControlledInputHandler.removeInputModifier(s -> s.startsWith(modifierId));
        }
    }

    private void startScript() {
        for (CodeScript script : scripts.values()) {
            if (script != this) {
                script.prepareStop();
            }
        }
        running = true;
    }

    private InputModifier<?> createModifier(String action, boolean value) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        try {
            Method method = InputModifier.Movement.class.getMethod(action, boolean.class, int.class, String.class);
            return (InputModifier.Movement) method.invoke(null, value, 0, modifierId + ":" + action);
        } catch (NoSuchMethodException e) {
            Method method = InputModifier.Key.class.getMethod(action, boolean.class, int.class, String.class);
            return (InputModifier.Key) method.invoke(null, value, 0, modifierId + ":" + action);
        }
    }

    protected interface Task {

        int run(World world, PlayerEntity player, int current);
    }
}
