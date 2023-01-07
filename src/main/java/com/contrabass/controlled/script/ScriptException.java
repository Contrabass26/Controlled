package com.contrabass.controlled.script;

public class ScriptException extends RuntimeException {

    public ScriptException(String message, String script, int line, Exception cause) {
        super("%s: %s at line %s".formatted(message, script, line));
        if (cause != null) {
            this.addSuppressed(cause);
        }
    }

    public ScriptException(String script, int line, Exception cause) {
        this("Error parsing script", script, line, cause);
    }

    public static class InvalidArgument extends ScriptException {

        public InvalidArgument(String argument, String script, int line) {
            super("Invalid argument '" + argument + "'", script, line, null);
        }
    }
}
