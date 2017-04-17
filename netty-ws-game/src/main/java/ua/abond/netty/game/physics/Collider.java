package ua.abond.netty.game.physics;

public interface Collider {

    boolean collides(Collider that);

    void onCollision(Collider that);

    Vector2 getPosition();

    String getMark();
}
