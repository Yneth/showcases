package ua.abond.netty.game.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Vector2 {
    public static final Vector2 ZERO = Vector2.builder().x(0f).y(0f).build();

    private float x;
    private float y;

    public Vector2 add(Vector2 that) {
        this.x += that.x;
        this.y += that.y;
        return this;
    }

    public Vector2 minus(Vector2 that) {
        return negate().add(that);
    }

    public Vector2 negate() {
        this.x = -this.x;
        this.y = -this.y;
        return this;
    }

    public Vector2 multiply(float val) {
        this.x *= val;
        this.y *= val;
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

    public float magnitude() {
        return (float) Math.sqrt(x * x + y * y);
    }

    public Vector2 clone() {
        return new Vector2(this.x, this.y);
    }

    @Override
    public String toString() {
        return x + "," + y;
    }
}
