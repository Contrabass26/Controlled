package com.contrabass.controlled;

import net.minecraft.client.input.Input;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public abstract class InputModifier implements Comparable<InputModifier>, Consumer<Input> {
    
    public final int priority;
    public final String id;
    
    private InputModifier(int priority, String id) {
        this.priority = priority;
        this.id = id;
    }

    @Override
    public int compareTo(@NotNull InputModifier other) {
        return Integer.compare(this.priority, other.priority);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other instanceof InputModifier that) {
            if (priority != that.priority) return false;
            return id.equals(that.id);
        }
        return false;
    }

    public static InputModifier jump(boolean jump, int priority, String id) {
        return new InputModifier(priority, id) {
            @Override
            public void accept(Input keyboardInput) {
                keyboardInput.jumping = jump;
            }
        };
    }

    public static InputModifier shift(boolean shift, int priority, String id) {
        return new InputModifier(priority, id) {
            @Override
            public void accept(Input keyboardInput) {
                keyboardInput.sneaking = shift;
            }
        };
    }

    public static InputModifier w(boolean pressed, int priority, String id) {
        return new InputModifier(priority, id) {
            @Override
            public void accept(Input keyboardInput) {
                keyboardInput.pressingForward = pressed;
            }
        };
    }

    public static InputModifier a(boolean pressed, int priority, String id) {
        return new InputModifier(priority, id) {
            @Override
            public void accept(Input keyboardInput) {
                keyboardInput.pressingLeft = pressed;
            }
        };
    }

    public static InputModifier s(boolean pressed, int priority, String id) {
        return new InputModifier(priority, id) {
            @Override
            public void accept(Input keyboardInput) {
                keyboardInput.pressingBack = pressed;
            }
        };
    }

    public static InputModifier d(boolean pressed, int priority, String id) {
        return new InputModifier(priority, id) {
            @Override
            public void accept(Input keyboardInput) {
                keyboardInput.pressingRight = pressed;
            }
        };
    }
}
