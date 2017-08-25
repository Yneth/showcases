package ua.abond.netty.game.domain;

import lombok.Setter;
import ua.abond.netty.game.physics.Transform;
import ua.abond.netty.game.physics.collision.Collidable;
import ua.abond.netty.game.physics.collision.Collider;
import ua.abond.netty.game.physics.collision.CollisionData;

@Setter
public class WallCollider implements Collidable {
    private final GameObject gameObject;

    private WallBulletCollisionListener wallBulletCollisionListener;

    public WallCollider(GameObject gameObject) {
        this.gameObject = gameObject;
    }

    @Override
    public Transform getTransform() {
        return gameObject.getTransform();
    }

    @Override
    public String getMark() {
        return "wall";
    }

    @Override
    public Collider getCollider() {
        return gameObject.getComponent(Collider.class);
    }

    @Override
    public void onCollision(Collidable other, CollisionData collisionData) {
        if ("bullet".equals(other.getMark())) {
            if (wallBulletCollisionListener == null) {
                return;
            }
            wallBulletCollisionListener.onCollision(gameObject.getComponent(WallBehaviour.class), (Bullet) other);
        } else if ("player".equals(other.getMark())) {

        }
    }
}
