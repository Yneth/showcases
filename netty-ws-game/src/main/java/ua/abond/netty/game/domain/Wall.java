package ua.abond.netty.game.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import ua.abond.netty.game.physics.Collider;
import ua.abond.netty.game.physics.Vector2;

@Data
@NoArgsConstructor
public class Wall implements Collider {
    private int width;
    private int height;
    private Vector2 center;

    @Override
    public boolean collides(Collider that) {
        if ("bullet".equals(that.getMark()) || "player".equals(that.getMark())) {
            return collidesWithCircle(that);
        }
        return false;
    }

    private boolean collidesWithCircle(Collider that) {
        float distX = Math.abs(that.getPosition().getX() - center.getX() - width * 0.5f);
        float distY = Math.abs(that.getPosition().getY() - center.getY() - height * 0.5f);
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
}
