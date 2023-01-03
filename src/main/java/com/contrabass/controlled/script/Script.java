package com.contrabass.controlled.script;

import com.contrabass.controlled.ControlledInputHandler;
import com.contrabass.controlled.InputModifier;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Script {

    private static final Map<String, Script> SCRIPTS = new HashMap<>();

    public enum Trigger {
        PLAYER_ON_GROUND
    }

    private int index = 0;
    /**
     * 0 = not running
     * 1 = running
     * 2 = waiting
     */
    private int state = 0;
    private final List<String> lines;
    private final String modifierId;
    private final List<Loop> loopStack = new ArrayList<>();
    private Trigger trigger = null;

    Script(Identifier identifier, ResourceManager manager) throws IOException {
        String name = identifier.getPath();
        name = name.substring(7, name.length() - 4);
        modifierId = "script:" + name;
        InputStream stream = manager.getResource(identifier).orElseThrow().getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        lines = reader.lines().toList();
        if (!lines.get(lines.size() - 1).equals("next")) {
            lines.add("next");
        }
    }

    public static void handleTrigger(Trigger trigger) {
        SCRIPTS.values().forEach(s -> s.trigger(trigger));
    }

    private void trigger(Trigger trigger) {
        if (this.trigger == trigger) {
            this.trigger = null;
        }
    }

    public void toggleRunning() {
        if (state == 0) {
            state = 1;
        } else {
            for (int i = 0; i < lines.size(); i++) {
                if (lines.get(i).contains("tail")) {
                    index = i + 1;
                    loopStack.clear();
                    break;
                }
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
        if (state == 1 && trigger == null) {
            while (!lines.get(index).toLowerCase().startsWith("next")) {
                if (!execute(lines.get(index))) break;
                index++;
            }
            index++;
            if (index == lines.size()) {
                index = 0;
                state = 0;
                loopStack.clear();
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
        String[] words = line.toLowerCase().split(" ");
        try {
            switch (words[0]) {
                case "wait" -> {
                    trigger = Trigger.valueOf(words[1].toUpperCase());
                    return false;
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
                case "use" -> ControlledInputHandler.doNextRightClick = true;
                default -> {
                    switch (words[1]) {
                        case "start" -> ControlledInputHandler.addInputModifier(createModifier(words[0]));
                        case "stop" -> ControlledInputHandler.removeInputModifier(s -> s.equals(modifierId + ":" + words[0].toLowerCase()));
                        default -> throw new ScriptException.InvalidArgument(words[1], modifierId.substring(7), this.index);
                    }
                }
            }
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException | ArrayIndexOutOfBoundsException e) {
            throw new ScriptException(modifierId.substring(7), this.index);
        }
        return true;
    }

    public static void clearScripts() {
        SCRIPTS.clear();
    }

    private InputModifier<?> createModifier(String command) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String methodName = command.toLowerCase();
        try {
            Method method = InputModifier.Movement.class.getMethod(methodName, boolean.class, int.class, String.class);
            return (InputModifier.Movement) method.invoke(null, true, 0, modifierId + ":" + methodName);
        } catch (NoSuchMethodException e) {
            Method method = InputModifier.Key.class.getMethod(methodName, boolean.class, int.class, String.class);
            return (InputModifier.Key) method.invoke(null, true, 0, modifierId + ":" + methodName);
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
