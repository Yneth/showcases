package ua.abond.netty.game.physics.collision.collider;

import lombok.Getter;
import lombok.Setter;
import ua.abond.netty.game.physics.Transform;
import ua.abond.netty.game.physics.Vector2;
import ua.abond.netty.game.physics.collision.Collidable;
import ua.abond.netty.game.physics.collision.Collider;

@Getter
@Setter
public class CircleCollider implements Collider {
    private final String mark;
    private final Transform transform;
    private final Collidable collidable;

    private float radius;

    public CircleCollider(Collidable collidable, Transform transform) {
        this(collidable, transform, "circle");
    }

    public CircleCollider(Collidable collidable, Transform transform, String mark) {
        this.mark = mark;
        this.transform = transform;
        this.collidable = collidable;
    }

    @Override
    public boolean collides(Collider that) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onCollision(Collider that) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Vector2 getPosition() {
        return transform.getPosition();
    }

    @Override
    public int width() {
        return Math.round(radius * 2);
    }

    @Override
    public int height() {
        return Math.round(radius * 2);
    }

    @Override
    public String getMark() {
        return mark;
    }
}
