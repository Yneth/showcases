package ua.abond.netty.game.physics.collision.collider;

import lombok.Getter;
import lombok.Setter;
import ua.abond.netty.game.physics.Transform;
import ua.abond.netty.game.physics.Vector2;
import ua.abond.netty.game.physics.collision.Collider;

@Getter
@Setter
public class RectCollider implements Collider {
    private final String mark;
    private final Transform transform;

    private int width;
    private int height;

    public RectCollider(Transform transform) {
        this(transform, "rect");
    }

    public RectCollider(Transform transform, String mark) {
        this.transform = transform;
        this.mark = mark;
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
        return width;
    }

    @Override
    public int height() {
        return height;
    }

    @Override
    public String getMark() {
        return mark;
    }
}
