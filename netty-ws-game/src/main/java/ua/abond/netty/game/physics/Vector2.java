package ua.abond.netty.game.physics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Vector2 {
    public static final Vector2 ZERO = new Vector2(0f, 0f);
    public static final Vector2 ONE = new Vector2(1f, 1f);

    public static final Vector2 UP = new Vector2(0f, -1f);
    public static final Vector2 DOWN = new Vector2(0f, 1f);
    public static final Vector2 RIGHT = new Vector2(1f, 0f);
    public static final Vector2 LEFT = new Vector2(-1f, 0f);

    private float x;
    private float y;

    public float dot(final Vector2 that) {
        return this.x * that.x + this.x * that.x;
    }

    public Vector2 project(final Vector2 that) {
        float dot = this.dot(that);
        float sqrMag = that.squareMagnitude();

        this.x = (dot / sqrMag) * that.x;
        this.y = (dot / sqrMag) * that.y;
        return this;
    }

    public Vector2 add(final Vector2 that) {
        this.x += that.x;
        this.y += that.y;
        return this;
    }

    public Vector2 minus(final Vector2 that) {
        return negate().add(that);
    }

    public Vector2 negate() {
        this.x = -this.x;
        this.y = -this.y;
        return this;
    }

    public float cross(Vector2 that) {
        return this.x * that.y - this.y * that.x;
    }

    public Vector2 multiply(Vector2 that) {
        this.x *= that.x;
        this.y *= that.y;
        return this;
    }

    public Vector2 multiply(float val) {
        this.x *= val;
        this.y *= val;
        return this;
    }

    public Vector2 abs() {
        this.x = Math.abs(x);
        this.y = Math.abs(y);
        return this;
    }

    public Vector2 normalize() {
        float magnitude = magnitude();
        if (magnitude <= 0.0f) {
            magnitude = 1.0f;
        }
        this.x /= magnitude;
        this.y /= magnitude;
        return this;
    }

    public Vector2 set(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public float magnitude() {
        return (float) Math.sqrt(x * x + y * y);
    }

    public float squareMagnitude() {
        return x * x + y * y;
    }

    public boolean isCloseTo(Vector2 that) {
        return isCloseTo(that, 0.001f);
    }

    public boolean isCloseTo(Vector2 that, float eps) {
        return areClose(this.x, that.x, eps) && areClose(this.y, that.y, eps);
    }

    private static boolean areClose(float x0, float x1, float eps) {
        return Math.abs(x1 - x0) <= eps;
    }

    public Vector2 copy() {
        return new Vector2(this.x, this.y);
    }

    @Override
    public String toString() {
        return x + "," + y;
    }
}
