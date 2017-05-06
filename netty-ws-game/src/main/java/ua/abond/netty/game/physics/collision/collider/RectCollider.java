package ua.abond.netty.game.physics.collision.collider;

import lombok.Getter;
import lombok.Setter;
import ua.abond.netty.game.physics.Vector2;
import ua.abond.netty.game.physics.collision.Collidable;
import ua.abond.netty.game.physics.collision.Collider;

@Getter
@Setter
public class RectCollider implements Collider {
    private final Collidable collidable;

    private int width;
    private int height;

    public RectCollider(Collidable collidable, int width, int height) {
        this.collidable = collidable;
        this.width = width;
        this.height = height;
    }

    @Override
    public Vector2 getPosition() {
        return collidable.getTransform().getPosition();
    }

    @Override
    public int width() {
        return width;
    }

    @Override
    public int height() {
        return height;
    }
}
