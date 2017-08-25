package ua.abond.netty.game.physics.collision;

import ua.abond.netty.game.domain.component.Component;
import ua.abond.netty.game.physics.Transform;

public interface Collidable extends Component {

    Transform getTransform();

    String getMark();

    Collider getCollider();

    void onCollision(Collidable other, CollisionData collisionData);
}
