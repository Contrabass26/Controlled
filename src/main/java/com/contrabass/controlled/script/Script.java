package com.contrabass.controlled.script;

import com.contrabass.controlled.ControlledInputHandler;
import com.contrabass.controlled.InputModifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class Script {

    private static final Map<String, Script> SCRIPTS = new HashMap<>();

    private int index = 0;
    private boolean running = false;
    private final List<String> lines;
    private final String modifierId;
    private final List<Loop> loopStack = new ArrayList<>();
    private Condition condition = null;
    private boolean waitingIf = false;
    private boolean consumed = false;
    private double accumulator = 0;

    private Script(Identifier identifier, ResourceManager manager) throws IOException {
        String name = identifier.getPath();
        name = name.substring(7, name.length() - 4);
        modifierId = "script:" + name;
        InputStream stream = manager.getResource(identifier).orElseThrow().getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        lines = reader.lines().map(String::strip).toList();
    }

    public void handleKeybind(boolean pressed) {
        if (pressed) {
            if (!consumed) {
                toggleRunning();
                consumed = true;
            }
        } else {
            consumed = false;
        }
    }

    public void toggleRunning() {
        if (!running) {
            stopAll();
            running = true;
        } else {
            for (int i = 0; i < lines.size(); i++) {
                if (lines.get(i).contains("tail")) {
                    index = i + 1;
                    loopStack.clear();
                    waitingIf = false;
                    break;
                }
            }
        }
    }

    public static void stopAll() {
        for (Script script : SCRIPTS.values()) {
            if (script.running) {
                script.toggleRunning();
            }
        }
    }

    public static void register(Identifier identifier, ResourceManager resourceManager) throws IOException {
        String path = identifier.getPath();
        SCRIPTS.put(path.substring(7, path.length() - 4), new Script(identifier, resourceManager));
    }

    public static Script get(String name) {
        return SCRIPTS.get(name);
    }

    private void advanceTick() {
        if (condition != null) {
            if (condition.test(MinecraftClient.getInstance().player)) {
                condition = null;
            }
        }
        if (running && condition == null) {
            while (!lines.get(index).startsWith("next")) {
                if (!execute(lines.get(index))) break;
                index++;
            }
            index++;
            if (index == lines.size()) {
                index = 0;
                running = false;
                loopStack.clear();
                ControlledInputHandler.removeInputModifier(s -> s.startsWith(modifierId));
                waitingIf = false;
            }
        }
    }

    public static void tick() {
        SCRIPTS.values().forEach(Script::advanceTick);
    }

    /**
     * @param line The line to parse and execute
     * @return Whether to continue executing
     */
    private boolean execute(String line) {
        if (line.startsWith("//")) return true;
        String[] words = line.split(" ");
        try {
            // If statement control commands
            if (line.equals("fi")) {
                waitingIf = false;
                return true;
            } else if (line.equals("else")) {
                waitingIf = !waitingIf;
                return true;
            } else if (waitingIf) {
                return true;
            }
            // One-time commands
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            assert player != null;
            switch (words[0]) {
                case "acc" -> accumulator = Double.parseDouble(words[1]);
                case "wait" -> {
                    condition = Condition.get(words);
                    if (condition.test(player)) {
                        condition = null;
                    } else {
                        return false;
                    }
                }
                case "if" -> {
                    Condition condition = Condition.get(words);
                    if (!condition.test(player)) {
                        waitingIf = true;
                    }
                }
                case "loop" -> loopStack.add(new Loop(this.index + 1, Integer.parseInt(words[1])));
                case "pool" -> {
                    int topPos = loopStack.size() - 1;
                    Loop topLoop = loopStack.get(topPos);
                    boolean end = topLoop.spin();
                    if (end) {
                        loopStack.remove(topPos);
                    } else {
                        this.index = topLoop.returnIndex - 1;
                    }
                }
                case "use" -> {
                    if (words.length == 1) ControlledInputHandler.doNextRightClick = 1;
                    else ControlledInputHandler.doNextRightClick = (words[1].equals("start") ? 2 : 0);
                }
                case "attack" -> {
                    if (words.length == 1) ControlledInputHandler.doNextLeftClick = 1;
                    else ControlledInputHandler.doNextLeftClick = (words[1].equals("stop") ? 2 : 0);
                }
                case "yaw" -> ControlledInputHandler.moveToYaw = Float.parseFloat(words[1]);
                case "pitch" -> ControlledInputHandler.moveToPitch = Float.parseFloat(words[1]);
                case "lockRotation" -> ControlledInputHandler.lockRotation();
                // Continuous commands
                default -> {
                    switch (words[1]) {
                        case "start" -> ControlledInputHandler.addInputModifier(createModifier(words[0], true));
                        case "stop" -> ControlledInputHandler.addInputModifier(createModifier(words[0], false));
                        default -> throw new ScriptException.InvalidArgument(words[1], modifierId.substring(7), this.index);
                    }
                }
            }
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException | ArrayIndexOutOfBoundsException e) {
            throw new ScriptException(modifierId.substring(7), this.index, e);
        }
        return true;
    }

    public static void clearScripts() {
        SCRIPTS.clear();
    }

    private InputModifier<?> createModifier(String command, boolean value) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        try {
            Method method = InputModifier.Movement.class.getMethod(command, boolean.class, int.class, String.class);
            return (InputModifier.Movement) method.invoke(null, value, 0, modifierId + ":" + command);
        } catch (NoSuchMethodException e) {
            Method method = InputModifier.Key.class.getMethod(command, boolean.class, int.class, String.class);
            return (InputModifier.Key) method.invoke(null, value, 0, modifierId + ":" + command);
        }
    }

    private static class Loop {

        public final int returnIndex;
        private int iterationsLeft;

        private Loop(int returnIndex, int iterations) {
            this.returnIndex = returnIndex;
            iterationsLeft = iterations;
        }

        /**
         * @return Whether the loop is finished
         */
        public boolean spin() {
            if (iterationsLeft != -1) {
                iterationsLeft--;
                return iterationsLeft == 0;
            }
            return false;
        }
    }
}
