package ua.abond.netty.game.domain;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import ua.abond.netty.game.physics.collision.Collider;
import ua.abond.netty.game.physics.Rect;
import ua.abond.netty.game.physics.Vector2;
import ua.abond.netty.game.physics.collision.spatial.quad.QuadNode;

@Data
@Builder
@ToString(of = "position")
public class Bullet implements Collider {
    private Player owner;

    private Vector2 position;

    private Vector2 direction;

    @Override
    public boolean collides(Collider that) {
        if ("wall".equals(that.getMark())) {
            return that.collides(this);
        } else if ("bullet".equals(that.getMark())) {
            return position.copy().add(that.getPosition().copy().negate()).squareMagnitude() <= 100f;
        }
        return position.copy().add(that.getPosition().copy().negate()).squareMagnitude() <= 625f;
    }

    @Override
    public void onCollision(Collider that) {
    }

    @Override
    public int width() {
        return 10;
    }

    @Override
    public int height() {
        return 10;
    }

    @Override
    public String getMark() {
        return "bullet";
    }

    public static QuadNode<Collider> toQuadNode(Bullet bullet) {
        return new QuadNode<>(bullet, Rect.from(bullet.getPosition(), 10, 10));
    }
}
