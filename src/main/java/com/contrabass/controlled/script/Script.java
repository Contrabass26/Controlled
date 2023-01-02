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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Script {

    private static final Map<String, Script> SCRIPTS = new HashMap<>();
    private static final int SCRIPT_START = 2;

    private int index = SCRIPT_START;
    private boolean isRunning = false;
    private final boolean loop;
    private final List<String> lines;
    private final String modifierId;

    Script(Identifier identifier, ResourceManager manager) throws IOException {
        String name = identifier.getPath();
        name = name.substring(7, name.length() - 4);
        modifierId = "script:" + name;
        InputStream stream = manager.getResource(identifier).orElseThrow().getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        lines = reader.lines().toList();
        loop = Boolean.parseBoolean(lines.get(0));
    }

    public void toggleRunning() {
        isRunning = !isRunning;
        index = SCRIPT_START;
    }

    public static void register(Identifier identifier, ResourceManager resourceManager) throws IOException {
        String path = identifier.getPath();
        SCRIPTS.put(path.substring(7, path.length() - 4), new Script(identifier, resourceManager));
    }

    public static Script get(String name) {
        return SCRIPTS.get(name);
    }

    private void advanceTick() {
        while (!lines.get(index).toLowerCase().startsWith("next")) {
            execute(lines.get(index));
            index++;
        }
        index++;
        if (index == lines.size()) {
            index = SCRIPT_START;
            if (!loop) isRunning = false;
        }
    }

    public static void tick() {
        for (Script script : SCRIPTS.values()) {
            if (script.isRunning) {
                script.advanceTick();
            }
        }
    }

    private void execute(String line) {
        if (line.startsWith("//")) return;
        String[] words = line.toLowerCase().split(" ");
        try {
            switch (words[1]) {
                case "start" -> ControlledInputHandler.addInputModifier(createModifier(words[0]));
                case "stop" -> ControlledInputHandler.removeInputModifier(s -> s.equals(modifierId + ":" + words[0].toLowerCase()));
                default -> throw new ScriptException.InvalidArgument(words[1], modifierId.substring(7), index);
            }
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException | ArrayIndexOutOfBoundsException e) {
            throw new ScriptException(modifierId.substring(7), index);
        }
    }

    public static void clearScripts() {
        SCRIPTS.clear();
    }

    private InputModifier createModifier(String command) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String methodName = command.toLowerCase();
        Method method = InputModifier.class.getMethod(methodName, boolean.class, int.class, String.class);
        return (InputModifier) method.invoke(null, true, 0, modifierId + ":" + methodName);
    }
}
