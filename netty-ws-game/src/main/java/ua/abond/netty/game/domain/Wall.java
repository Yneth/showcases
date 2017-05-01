package ua.abond.netty.game.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import ua.abond.netty.game.physics.collision.Collider;
import ua.abond.netty.game.physics.Rect;
import ua.abond.netty.game.physics.Vector2;
import ua.abond.netty.game.physics.collision.spatial.quad.QuadNode;

@Data
@NoArgsConstructor
public class Wall implements Collider {
    private Vector2 center;
    private int width;
    private int height;

    private WallBulletCollisionHandler collisionHandler;

    public Wall(Vector2 center, int width, int height) {
        this.center = center;
        this.width = width;
        this.height = height;
    }

    @Override
    public boolean collides(Collider that) {
        if ("bullet".equals(that.getMark()) || "player".equals(that.getMark())) {
            return collidesWithCircle(that);
        }
        return false;
    }

    private boolean collidesWithCircle(Collider that) {
        float distX = Math.abs(that.getPosition().getX() - center.getX());
        float distY = Math.abs(that.getPosition().getY() - center.getY());
        if (distX > (width + that.width()) * 0.5f) {
            return false;
        }
        if (distY > (height + that.height()) * 0.5f) {
            return false;
        }
        if (distX <= width * 0.5f) {
            return true;
        }
        if (distY <= height * 0.5f) {
            return true;
        }
        float dx = distX - width * 0.5f;
        float dy = distY - height * 0.5f;
        return dx * dx + dy * dy <= that.width() * 0.5f;
    }

    @Override
    public void onCollision(Collider that) {
        if ("bullet".equals(that.getMark())) {
            if (collisionHandler == null) {
                return;
            }

            collisionHandler.onCollision(this, (Bullet) that);
        }
    }

    @Override
    public Vector2 getPosition() {
        return center;
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
        return "wall";
    }

    public static QuadNode<Collider> toQuadNode(Wall wall) {
        Vector2 position = wall.getPosition();
        return new QuadNode<>(
                wall,
                new Rect(
                        position.getX(),
                        position.getY(),
                        wall.getWidth(),
                        wall.getHeight()
                )
        );
    }
}
