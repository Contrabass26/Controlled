package com.contrabass.controlled.script;

public class ScriptException extends RuntimeException {

    public ScriptException(String message, String script, int line) {
        super("%s: %s at line %s".formatted(message, script, line));
    }

    public ScriptException(String script, int line) {
        this("Error parsing script", script, line);
    }

    public static class InvalidArgument extends ScriptException {

        public InvalidArgument(String argument, String script, int line) {
            super("Invalid argument '" + argument + "'", script, line);
        }
    }
}
