package ua.abond.netty.game.physics.collision.collider;

import lombok.AllArgsConstructor;
import lombok.Data;
import ua.abond.netty.game.physics.Vector2;

@Data
@AllArgsConstructor
public class Rect {
    private float x;
    private float y;

    private float width;
    private float height;

    public boolean isInside(Rect that) {
        return this.getX() >= that.getX() && this.getX() + this.getWidth() <= (that.getX() + that.getWidth()) &&
                this.getY() >= that.getY() && this.getY() + this.getHeight() <= (that.getY() + that.getHeight());
    }

    public static Rect from(Vector2 position, int w, int h) {
        float subWidth = w / 2f;
        float subHeight = h / 2f;
        return new Rect(position.getX() - subWidth, position.getY() - subHeight, w, h);
    }
}
