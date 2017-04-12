package ua.abond.netty.game.domain;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class Player {
    private Vector2 position;
    private Vector2 target = Vector2.ZERO;

    private int screenWidth;
    private int screenHeight;

    private String name;
}
