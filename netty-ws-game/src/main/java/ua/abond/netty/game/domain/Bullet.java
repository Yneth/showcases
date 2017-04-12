package ua.abond.netty.game.domain;

import lombok.Builder;

@Builder
public class Bullet {
    private Vector2 position;

    private Vector2 size;
}
