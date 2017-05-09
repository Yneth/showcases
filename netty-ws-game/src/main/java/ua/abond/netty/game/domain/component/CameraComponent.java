package ua.abond.netty.game.domain.component;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import ua.abond.netty.game.physics.Transform;
import ua.abond.netty.game.physics.Vector2;

@Setter
@Getter
public class CameraComponent implements Component {
    private final Transform parent;

    private float speed;
    @Setter(AccessLevel.NONE)
    private Vector2 position;

    private int width = 500;
    private int height = 500;

    private int xMin = 0;
    private int xMax = 1000;
    private int yMin = 0;
    private int yMax = 1000;

    private boolean updated = true;

    public CameraComponent(Transform parent, float speed) {
        this.parent = parent;
        this.position = parent.getPosition().copy();
        this.position.set(
                clamp(position.getX(), xMin + (width * 0.5f), xMax - (width * 0.5f)),
                clamp(position.getY(), yMin + (height * 0.5f), yMax - (height * 0.5f))
        );
        this.speed = speed;
    }

    private float clamp(float value, float min, float max) {
        return Math.max(Math.min(value, max), min);
    }

    @Override
    public void update(float deltaTime) {
//        Vector2 dst = parent.getPosition().copy().add(position.copy().negate());
//        if (dst.squareMagnitude() > 10000) {
//        position.add(dst.normalize().multiply(speed * deltaTime));
//        position.set(
//                clamp(position.getX(), xMin + (width * 0.5f), xMax - (width * 0.5f)),
//                clamp(position.getY(), yMin + (height * 0.5f), yMax - (height * 0.5f))
//        );
//        updated = true;
//        }
        updated = true;
        Vector2 parentPosition = parent.getPosition();
        position.set(parentPosition.getX(), parentPosition.getY());
        position.set(
                clamp(position.getX(), xMin + (width * 0.5f), xMax - (width * 0.5f)),
                clamp(position.getY(), yMin + (height * 0.5f), yMax - (height * 0.5f))
        );
    }
}
