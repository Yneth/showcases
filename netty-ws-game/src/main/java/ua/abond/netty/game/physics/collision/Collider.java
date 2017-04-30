package ua.abond.netty.game.physics.collision;

import ua.abond.netty.game.physics.Vector2;

public interface Collider {

    boolean collides(Collider that);

    void onCollision(Collider that);

    Vector2 getPosition();

    int width();

    int height();

    String getMark();
}
