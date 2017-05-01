package ua.abond.netty.game.physics.collision;

public interface Collidable {

    Collider getCollider();

    void onCollision(Collidable other, CollisionData collisionData);
}
