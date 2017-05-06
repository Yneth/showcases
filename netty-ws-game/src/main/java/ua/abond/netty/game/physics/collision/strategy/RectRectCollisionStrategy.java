package ua.abond.netty.game.physics.collision.strategy;

import ua.abond.netty.game.physics.collision.CollisionService;
import ua.abond.netty.game.physics.collision.CollisionData;
import ua.abond.netty.game.physics.collision.collider.RectCollider;

public class RectRectCollisionStrategy
        implements CollisionService<RectCollider, RectCollider> {

    @Override
    public boolean collides(RectCollider c0, RectCollider c1, CollisionData collisionData) {
        return false;
    }
}
