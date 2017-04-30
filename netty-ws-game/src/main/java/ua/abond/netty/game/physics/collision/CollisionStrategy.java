package ua.abond.netty.game.physics.collision;

import ua.abond.netty.game.physics.collision.collider.CollisionData;

public interface CollisionStrategy<A extends Collider, B extends Collider> {
    boolean collides(A c0, B c1, CollisionData collisionData);
}
