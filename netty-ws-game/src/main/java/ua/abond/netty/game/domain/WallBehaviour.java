package ua.abond.netty.game.domain;

import ua.abond.netty.game.domain.component.Behaviour;

public class WallBehaviour implements Behaviour {
    private final GameObject gameObject;

    public WallBehaviour(GameObject gameObject) {
        this.gameObject = gameObject;
    }

    @Override
    public void update(float delta) {

    }
}
