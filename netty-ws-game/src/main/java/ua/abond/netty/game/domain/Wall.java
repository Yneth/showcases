package ua.abond.netty.game.domain;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.abond.netty.game.physics.Collider;
import ua.abond.netty.game.physics.Vector2;

@Data
@Builder
@NoArgsConstructor
public class Wall implements Collider {
    private int width;
    private int height;
    private Vector2 position;

    @Override
    public boolean collides(Collider that) {
        if ("bullet".equals(that.getMark())) {
//            return
        } else if ("player".equals(that.getMark())) {

        }
        return false;
    }

    @Override
    public void onCollision(Collider that) {

    }

    @Override
    public Vector2 getPosition() {
        return position;
    }

    @Override
    public String getMark() {
        return "wall";
    }
}
