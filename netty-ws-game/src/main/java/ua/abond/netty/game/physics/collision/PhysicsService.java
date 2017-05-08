package ua.abond.netty.game.physics.collision;

import ua.abond.netty.game.physics.Vector2;

import java.util.List;

public interface PhysicsService {

    void update(float delta);

    void add(Collider collider);

    void remove(Collider collider);

    List<Collider> viewFrustum(final Vector2 position, int width, int height);
}
