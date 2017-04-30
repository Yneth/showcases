package ua.abond.netty.game.physics.collision.collider;

import ua.abond.netty.game.physics.Vector2;
import ua.abond.netty.game.physics.collision.Collider;

public class CircleCollider implements Collider {
    private float radius;

    @Override
    public boolean collides(Collider that) {
        return false;
    }

    @Override
    public void onCollision(Collider that) {

    }

    @Override
    public Vector2 getPosition() {
        return null;
    }

    @Override
    public int width() {
        return Math.round(radius * 2);
    }

    @Override
    public int height() {
        return Math.round(radius * 2);
    }

    @Override
    public String getMark() {
        return null;
    }
}
