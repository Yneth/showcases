package ua.abond.netty.game.physics.collision;

public interface CollisionService<A extends Collider, B extends Collider> {
    boolean collides(A c0, B c1, CollisionData collisionData);
}
