package com.contrabass.controlled;

import net.minecraft.util.math.Vec3d;
import org.joml.Vector2d;

// 2D Vector specification:
// x = east (positive) or west (negative)
// z = south (positive) or north (negative)
public class MathUtils {

    private MathUtils() {}

    public static boolean[] directionToKeys(Vector2d direction, float yaw) {
        // Yaw = rotation clockwise from south
        // If the yaw was zero, there would be no need to modify the direction
        // Suppose the yaw was 90 (facing west) and we want to move 1 unit south:
        // Because we're facing west, we actually want to press the 'a' key
        // So we need to rotate the yaw by -90° (negative yaw) clockwise
        // Or (360 - yaw)° anticlockwise
        Vector2d rotated = rotate(direction, 360 - yaw);
        return new boolean[]{
                rotated.y > 0,
                rotated.x < 0,
                rotated.y < 0,
                rotated.x > 0
        };
    }

    public static boolean[] getKeysFor(Vector2d initial, Vector2d finish, float yaw) {
        return directionToKeys(finish.sub(initial), yaw);
    }

    public static Vector2d rotate(Vector2d v, float angle) {
        double cos = cos(angle);
        double sin = sin(angle);
        return new Vector2d(
                cos * v.x - sin * v.y,
                sin * v.x + cos * v.y);
    }

    public static double cos(double theta) {
        return Math.cos(Math.toRadians(theta));
    }

    public static double sin(double theta) {
        return Math.sin(Math.toRadians(theta));
    }

    public static Vector2d flatten(Vec3d v) {
        return new Vector2d(v.x, v.z);
    }

    public static double roundToZero(double d, double interval) {
        return roundToZero(d / interval) * interval;
    }

    public static double roundToZero(double d) {
        return d < 0 ? Math.ceil(d) : Math.floor(d);
    }

    public static double roundFromZero(double d, double interval) {
        return roundFromZero(d / interval) * interval;
    }

    public static double roundFromZero(double d) {
        return d < 0 ? Math.floor(d) : Math.ceil(d);
    }

    public static double addPlusMinus(double a, double b) {
        return a < 0 ? (a - b) : (a + b);
    }

    public static double normalise(double d, double max) {
        return d < 0 ? (d + max) : d;
    }
}
