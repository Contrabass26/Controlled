package com.contrabass.controlled.script;

import com.contrabass.controlled.util.ControlledUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.function.Predicate;

public interface Condition extends Predicate<PlayerEntity> {

    /**
     * @return [INDIRECT] Whether the player is on the ground
     */
    static Condition playerOnGround() {
        return PlayerEntity::isOnGround;
    }

    /**
     * @param minStr The minimum distance from the last full block
     * @param maxStr The maximum distance from the last full block
     * @return [INDIRECT] Whether the distance from the last full block falls between the specified bounds (inclusive)
     * @throws InvocationTargetException Error finding expression
     * @throws NoSuchMethodException Error finding expression
     * @throws IllegalAccessException Error finding expression
     */
    static Condition distanceBackwards(String minStr, String maxStr) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Expression minExpression = parseArgument(minStr);
        Expression maxExpression = parseArgument(maxStr);
        return player -> {
            double distance = ControlledUtils.getDistanceBackwards(player);
            double min = Double.parseDouble(minExpression.apply(player));
            double max = Double.parseDouble(maxExpression.apply(player));
            return min <= distance && distance <= max;
        };
    }

    static Condition distanceFromLeft(String minStr, String maxStr) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Expression minExpression = parseArgument(minStr);
        Expression maxExpression = parseArgument(maxStr);
        return player -> {
            double distance = ControlledUtils.getDistanceBackwards(player.getPos(), player.getHorizontalFacing().rotateYClockwise());
            double min = Double.parseDouble(minExpression.apply(player));
            double max = Double.parseDouble(maxExpression.apply(player));
            return min <= distance && distance <= max;
        };
    }

    /**
     * @param sideStr The Direction, in String form, of the face the player should be targeting
     * @return [INDIRECT] Whether the player has is targeting the specified side of a block
     * @throws InvocationTargetException Error finding expression
     * @throws NoSuchMethodException Error finding expression
     * @throws IllegalAccessException Error finding expression
     */
    static Condition targetingBlock(String sideStr) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Expression expression = sideStr.equals("any") ? null : parseArgument(sideStr);
        return player -> {
            if (expression == null) {
                return MinecraftClient.getInstance().crosshairTarget instanceof BlockHitResult;
            }
            Direction side = Direction.valueOf(expression.apply(player));
            return MinecraftClient.getInstance().crosshairTarget instanceof BlockHitResult hitResult && hitResult.getSide() == side;
        };
    }

    static Condition get(String[] words) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        String triggerName = words[1];
        Class<?>[] argsClass = new Class<?>[words.length - 2];
        Arrays.fill(argsClass, String.class);
        String[] args = new String[words.length - 2];
        System.arraycopy(words, 2, args, 0, args.length);
        return (Condition) Condition.class.getMethod(triggerName, argsClass).invoke(null, (Object[]) args);
    }

    private static Expression parseArgument(String argument) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (argument.startsWith("$")) {
            return (Expression) Expression.class.getMethod(argument.substring(1)).invoke(null);
        }
        return player -> argument;
    }
}
