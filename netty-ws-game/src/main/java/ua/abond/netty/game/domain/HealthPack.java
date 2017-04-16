package ua.abond.netty.game.domain;

import lombok.Builder;
import ua.abond.netty.game.physics.Vector2;

@Builder
public class HealthPack {
    private Vector2 position;
}
