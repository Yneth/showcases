package ua.abond.netty.game.physics.collision.strategy;

import ua.abond.netty.game.physics.Vector2;
import ua.abond.netty.game.physics.collision.CollisionService;
import ua.abond.netty.game.physics.collision.collider.CircleCollider;
import ua.abond.netty.game.physics.collision.CollisionData;
import ua.abond.netty.game.physics.collision.collider.RectCollider;

public class CircleRectCollisionStrategy
        implements CollisionService<CircleCollider, RectCollider> {

    @Override
    public boolean collides(CircleCollider c0, RectCollider c1, CollisionData collisionData) {
        Vector2 dist = c0.getPosition().copy().add(c1.getPosition().copy().negate());

        Vector2 sign = new Vector2(Math.signum(dist.getX()), Math.signum(dist.getY()));

        Vector2 rectRotation = c1.getTransform().getRotation();
        Vector2 dist2 = new Vector2(
                Math.abs(dist.getX() * rectRotation.getX() - dist.getY() * rectRotation.getY()),
                Math.abs(dist.getY() * rectRotation.getX() + dist.getX() * rectRotation.getY())
        );

        if (dist2.getX() > (c1.width() + c0.width()) * 0.5f) {
            return false;
        }
        if (dist2.getY() > (c1.height() + c0.height()) * 0.5f) {
            return false;
        }
        if (dist2.getX() <= c1.width() * 0.5f) {
            collisionData.setContactPoint(translatePointOnBox(
                    c1.getPosition(), rectRotation, new Vector2(c1.width() * 0.5f, dist.getY()), sign
            ));
            return true;
        }
        if (dist2.getY() <= c1.height() * 0.5f) {
            collisionData.setContactPoint(translatePointOnBox(
                    c1.getPosition(), rectRotation, new Vector2(dist.getX(), c1.height() * 0.5f), sign
            ));
            return true;
        }
        float dx = dist2.getX() - c1.width() * 0.5f;
        float dy = dist2.getY() - c1.height() * 0.5f;
        if (dx * dx + dy * dy <= c0.width() * 0.5f) {
            collisionData.setContactPoint(translatePointOnBox(
                    c1.getPosition(), rectRotation, new Vector2(c1.width() * 0.5f, c1.height() * 0.5f), sign
            ));
            return true;
        }
        return false;
    }

    private Vector2 translatePointOnBox(Vector2 position, Vector2 rotation, Vector2 unsignedLocal, Vector2 sign) {
        Vector2 local = unsignedLocal.copy().multiply(sign);
        return new Vector2(
                local.getX() * rotation.getX() - local.getY() * rotation.getY(),
                local.getY() * rotation.getX() + local.getX() * rotation.getY()
        ).add(position);
    }
}
