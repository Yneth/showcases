package ua.abond.netty.game.physics.collision.collider;

import lombok.Getter;
import lombok.Setter;
import ua.abond.netty.game.physics.Vector2;
import ua.abond.netty.game.physics.collision.Collidable;
import ua.abond.netty.game.physics.collision.Collider;

@Getter
@Setter
public class CircleCollider implements Collider {
    private final Collidable collidable;

    private float radius;

    public CircleCollider(Collidable collidable, float radius) {
        this.collidable = collidable;
        this.radius = radius;
    }

    @Override
    public Vector2 getPosition() {
        return collidable.getTransform().getPosition();
    }

    @Override
    public int width() {
        return Math.round(radius * 2);
    }

    @Override
    public int height() {
        return Math.round(radius * 2);
    }
}
