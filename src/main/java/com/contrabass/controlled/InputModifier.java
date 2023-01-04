package com.contrabass.controlled;

import net.minecraft.client.input.Input;
import net.minecraft.client.option.GameOptions;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public abstract class InputModifier<T> implements Comparable<InputModifier<?>>, Consumer<T> {
    
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
            return id.equals(that.id);
        }
        return false;
    }

    public abstract static class Key extends InputModifier<GameOptions> {

        private Key(int priority, String id) {
            super(priority, id);
        }

        public static Key sprint(boolean sprint, int priority, String id) {
            return new Key(priority, id) {
                @Override
                public void accept(GameOptions gameOptions) {
                    gameOptions.sprintKey.setPressed(sprint);
                }
            };
        }
    }
    
    public abstract static class Movement extends InputModifier<Input> {
        
        private Movement(int priority, String id) {
            super(priority, id);
        }

        public static Movement jump(boolean jump, int priority, String id) {
            return new Movement(priority, id) {
                @Override
                public void accept(Input keyboardInput) {
                    keyboardInput.jumping = jump;
                }
            };
        }

        public static Movement shift(boolean shift, int priority, String id) {
            return new Movement(priority, id) {
                @Override
                public void accept(Input keyboardInput) {
                    keyboardInput.sneaking = shift;
                }
            };
        }

        public static Movement w(boolean pressed, int priority, String id) {
            return new Movement(priority, id) {
                @Override
                public void accept(Input keyboardInput) {
                    keyboardInput.pressingForward = pressed;
                }
            };
        }

        public static Movement a(boolean pressed, int priority, String id) {
            return new Movement(priority, id) {
                @Override
                public void accept(Input keyboardInput) {
                    keyboardInput.pressingLeft = pressed;
                }
            };
        }

        public static Movement s(boolean pressed, int priority, String id) {
            return new Movement(priority, id) {
                @Override
                public void accept(Input keyboardInput) {
                    keyboardInput.pressingBack = pressed;
                }
            };
        }

        public static Movement d(boolean pressed, int priority, String id) {
            return new Movement(priority, id) {
                @Override
                public void accept(Input keyboardInput) {
                    keyboardInput.pressingRight = pressed;
                }
            };
        }
    }
}
