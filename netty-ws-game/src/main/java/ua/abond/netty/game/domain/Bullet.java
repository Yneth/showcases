package ua.abond.netty.game.domain;

import lombok.Builder;
import lombok.Data;
import ua.abond.netty.game.physics.Transform;
import ua.abond.netty.game.physics.Vector2;
import ua.abond.netty.game.physics.collision.Collidable;
import ua.abond.netty.game.physics.collision.Collider;
import ua.abond.netty.game.physics.collision.CollisionData;

@Data
@Builder
public class Bullet implements Collidable {
    private Player owner;

    private Vector2 direction;
    private Transform transform;

    private Collider collider;

    @Override
    public String getMark() {
        return "bullet";
    }

    @Override
    public void onCollision(Collidable other, CollisionData collisionData) {
    }
}
