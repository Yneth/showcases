package ua.abond.netty.game.physics.collision.collider;

import ua.abond.netty.game.physics.Vector2;
import ua.abond.netty.game.physics.collision.Collider;

public class RectCollider implements Collider {

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
        return 0;
    }

    @Override
    public int height() {
        return 0;
    }

    @Override
    public String getMark() {
        return null;
    }
}
