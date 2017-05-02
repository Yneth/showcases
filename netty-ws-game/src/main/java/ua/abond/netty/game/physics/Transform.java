package ua.abond.netty.game.physics;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Transform {
    private Vector2 position;
    private Vector2 rotation;
    private Vector2 scale;

    public Transform(Vector2 position) {
        this(position, Vector2.ONE);
    }

    public Transform(Vector2 position, Vector2 rotation) {
        this.position = position;
        this.rotation = rotation;
    }
}
