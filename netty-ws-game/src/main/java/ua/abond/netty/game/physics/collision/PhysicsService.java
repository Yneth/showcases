package ua.abond.netty.game.physics.collision;

public interface PhysicsService {

    void update(float delta);

    void add(Collider collider);

    void remove(Collider collider);
}
