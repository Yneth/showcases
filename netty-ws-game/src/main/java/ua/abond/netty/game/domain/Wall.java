package ua.abond.netty.game.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import ua.abond.netty.game.physics.Transform;
import ua.abond.netty.game.physics.Vector2;
import ua.abond.netty.game.physics.collision.Collidable;
import ua.abond.netty.game.physics.collision.Collider;
import ua.abond.netty.game.physics.collision.CollisionData;

@Data
@NoArgsConstructor
public class Wall implements Collidable {
    private Transform transform;
    private Collider collider;

    private WallBulletCollisionListener wallBulletCollisionHandler;

    public Wall(Vector2 center) {
        this.transform = new Transform(center);
    }

    @Override
    public String getMark() {
        return "wall";
    }

    @Override
    public void onCollision(Collidable other, CollisionData collisionData) {
        if ("bullet".equals(other.getMark())) {
            if (wallBulletCollisionHandler == null) {
                return;
            }
//            wallBulletCollisionHandler.onCollision(this, (Bullet) other);
        } else if ("player".equals(other.getMark())) {

        }
    }
}
