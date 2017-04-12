package ua.abond.netty.game.domain;

import lombok.Builder;

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

    public Vector2 multiply(float val) {
        this.x *= val;
        this.y *= val;
        return this;
    }

    public Vector2 normalize() {
        float magnitude = magnitude();
        this.x /= magnitude;
        this.y /= magnitude;
        return this;
    }

    public float magnitude() {
        return (float) Math.sqrt(x * x + y * y);
    }

    @Override
    public String toString() {
        return x + "," + y;
    }
}
