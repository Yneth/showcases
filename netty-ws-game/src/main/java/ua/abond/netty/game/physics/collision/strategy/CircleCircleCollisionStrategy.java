package ua.abond.netty.game.physics.collision.strategy;

import ua.abond.netty.game.physics.Vector2;
import ua.abond.netty.game.physics.collision.CollisionService;
import ua.abond.netty.game.physics.collision.collider.CircleCollider;
import ua.abond.netty.game.physics.collision.CollisionData;

public class CircleCircleCollisionStrategy
        implements CollisionService<CircleCollider, CircleCollider> {

    @Override
    public boolean collides(CircleCollider c0, CircleCollider c1, CollisionData collisionData) {
        Vector2 p0 = c0.getPosition().copy();
        Vector2 p1 = c1.getPosition().copy();
        float dst = c0.getRadius() + c1.getRadius();
        return p0.add(p1.negate()).squareMagnitude() <= (dst * dst);
    }
}
