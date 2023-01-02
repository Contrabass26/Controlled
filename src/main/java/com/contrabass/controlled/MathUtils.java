package com.contrabass.controlled;

import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.joml.Vector2d;
import org.joml.Vector3f;

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

    public static Vector2d flatten(Vec3i v) {
        return new Vector2d(v.getX(), v.getZ());
    }

    public static Vector2d flatten(Vector3f v) {
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
    
    public static Vector2d roundInDirection(Vector2d v, Direction direction) {
        Vector2d d = flatten(direction.getUnitVector());
        double x;
        double y;
        if (d.x == 0) {
            x = v.x;
        } else if (d.x < 0) {
            x = Math.floor(v.x);
        } else {
            x = Math.ceil(v.x);
        }
        if (d.y == 0) {
            y = v.y;
        } else if (d.y < 0) {
            y = Math.floor(v.y);
        } else {
            y = Math.ceil(v.y);
        }
        return new Vector2d(x, y);
    }

    public static double getNonZeroPart(Vector2d v) {
        return v.x == 0 ? v.y : v.x;
    }

    public static double addPlusMinus(double a, double b) {
        return a < 0 ? (a - b) : (a + b);
    }

    public static Vector2d addPlusMinus(Vector2d a, Vector2d b) {
        return new Vector2d(addPlusMinus(a.x, b.x), addPlusMinus(a.y, b.y));
    }

    public static double normalise(double d, double max) {
        return d < 0 ? (d + max) : d;
    }
}
