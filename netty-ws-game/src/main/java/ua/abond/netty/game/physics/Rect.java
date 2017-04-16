package ua.abond.netty.game.physics;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Rect {
    private int x;
    private int y;

    private int width;
    private int height;

    public boolean isInside(Rect that) {
        return this.getX() >= that.getX() && this.getX() + this.getWidth() <= (that.getX() + that.getWidth()) &&
                this.getY() >= that.getY() && this.getY() + this.getHeight() <= (that.getY() + that.getHeight());
    }
}
