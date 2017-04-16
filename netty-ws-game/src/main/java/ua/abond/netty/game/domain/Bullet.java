package ua.abond.netty.game.domain;

import lombok.Builder;
import lombok.Data;
import ua.abond.netty.game.physics.Vector2;

@Data
@Builder
public class Bullet {
    private Player owner;

    private Vector2 position;

    private Vector2 direction;
}
