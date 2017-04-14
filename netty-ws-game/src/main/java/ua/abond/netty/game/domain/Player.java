package ua.abond.netty.game.domain;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class Player {
    private Vector2 position;
    private Vector2 target;

    private int screenWidth;
    private int screenHeight;

    private String name;

    @Override
    public boolean equals(Object o) {
        return false;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (position != null ? position.hashCode() : 0);
        result = 31 * result + (target != null ? target.hashCode() : 0);
        result = 31 * result + screenWidth;
        result = 31 * result + screenHeight;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
