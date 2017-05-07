package ua.abond.netty.game.domain;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import ua.abond.netty.game.physics.Transform;
import ua.abond.netty.game.physics.Vector2;
import ua.abond.netty.game.physics.collision.Collidable;
import ua.abond.netty.game.physics.collision.Collider;
import ua.abond.netty.game.physics.collision.CollisionData;

@Data
@Builder
@ToString(of = {"name"})
@EqualsAndHashCode(of = "name")
public class Player implements Collidable {
    private Vector2 direction;

    private int screenWidth;
    private int screenHeight;

    private Transform transform;
    private Collider collider;

    private String name;

    private BulletCollisionHandler bulletCollisionHandler;

    @Override
    public String getMark() {
        return "player";
    }

    @Override
    public void onCollision(Collidable that, CollisionData collisionData) {
        Collider collider = that.getCollider();
        if ("bullet".equals(that.getMark())) {
            Bullet bullet = (Bullet) that;
            if (bullet.getOwner().equals(this)) {
                return;
            }
            bulletCollisionHandler.onCollision(this, bullet);
        } else if ("wall".equals(that.getMark())) {
            Vector2 contactPoint = collisionData.getContactPoint();
        }
    }
}
