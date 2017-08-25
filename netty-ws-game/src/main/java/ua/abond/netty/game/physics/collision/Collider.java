package ua.abond.netty.game.physics.collision;

import ua.abond.netty.game.domain.component.Component;
import ua.abond.netty.game.physics.Vector2;

public interface Collider extends Component {

    Vector2 getPosition();

    int width();

    int height();

    Collidable getCollidable();
}
