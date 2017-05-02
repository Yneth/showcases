package ua.abond.netty.game.physics.collision.service;

import ua.abond.netty.game.physics.collision.Collider;
import ua.abond.netty.game.physics.collision.PhysicsService;
import ua.abond.netty.game.physics.collision.SpatialIndex;
import ua.abond.netty.game.physics.collision.CollisionData;
import ua.abond.netty.game.physics.collision.spatial.quad.QuadNode;

public class PhysicsServiceImpl implements PhysicsService {
    private SpatialIndex<Collider, QuadNode<Collider>> spatialIndex;

    private CollisionStrategyService collisionStrategyService;

    public PhysicsServiceImpl(SpatialIndex<Collider, QuadNode<Collider>> spatialIndex) {
        this.spatialIndex = spatialIndex;
        this.collisionStrategyService = new CollisionStrategyService();
    }

    @Override
    public void update(float delta) {
        CollisionData collisionData = new CollisionData();

        spatialIndex.forEach((a, b) -> {
            if (collisionStrategyService.checkCollision(a, b, collisionData)) {
                a.onCollision(b);
            }
            collisionData.reset();
        });
    }

    @Override
    public void add(Collider collider) {
        spatialIndex.add(collider);
    }

    @Override
    public void remove(Collider collider) {
        spatialIndex.remove(collider);
    }
}
