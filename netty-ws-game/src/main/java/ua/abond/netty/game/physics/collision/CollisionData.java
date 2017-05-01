package ua.abond.netty.game.physics.collision;

import lombok.Getter;
import lombok.Setter;
import ua.abond.netty.game.physics.Vector2;

@Getter
@Setter
public class CollisionData {
    private Vector2 normal;
    private Vector2 contactPoint;

    public void reset() {
        normal = null;
        contactPoint = null;
    }
}
