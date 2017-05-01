package ua.abond.netty.game.domain;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import ua.abond.netty.game.physics.collision.Collider;
import ua.abond.netty.game.physics.Rect;
import ua.abond.netty.game.physics.Vector2;
import ua.abond.netty.game.physics.collision.spatial.quad.QuadNode;

@Data
@Builder
@ToString(of = {"name", "position"})
@EqualsAndHashCode(of = "name")
public class Player implements Collider {
    private Vector2 position;
    private Vector2 target;
    private Vector2 rotation;

    private int screenWidth;
    private int screenHeight;

    private String name;

    private BulletCollisionHandler bulletCollisionHandler;

    @Override
    public boolean collides(Collider that) {
        if ("wall".equals(that.getMark())) {
            return that.collides(this);
        } else if ("bullet".equals(that.getMark())) {
            return position.copy().add(that.getPosition().copy().negate()).squareMagnitude() <= 625f;
        }
        return position.copy().add(that.getPosition().copy().negate()).squareMagnitude() <= 1600f;
    }

    @Override
    public void onCollision(Collider that) {
        if ("bullet".equals(that.getMark())) {
            Bullet bullet = (Bullet) that;
            if (bullet.getOwner().equals(this)) {
                return;
            }
            bulletCollisionHandler.onCollision(this, bullet);
        } else if ("wall".equals(that.getMark())) {
            Vector2 position = that.getPosition();
            that.width();
        }
    }

    @Override
    public int width() {
        return 40;
    }

    @Override
    public int height() {
        return 40;
    }

    @Override
    public String getMark() {
        return "player";
    }

    public static QuadNode<Collider> toQuadNode(Player player) {
        return new QuadNode<>(player, Rect.from(player.getPosition(), 40, 40));
    }
}
