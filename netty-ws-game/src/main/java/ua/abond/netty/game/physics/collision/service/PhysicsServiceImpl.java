package ua.abond.netty.game.physics.collision.service;

import ua.abond.netty.game.physics.Rect;
import ua.abond.netty.game.physics.Vector2;
import ua.abond.netty.game.physics.collision.Collider;
import ua.abond.netty.game.physics.collision.CollisionData;
import ua.abond.netty.game.physics.collision.PhysicsService;
import ua.abond.netty.game.physics.collision.SpatialIndex;
import ua.abond.netty.game.physics.collision.spatial.quad.QuadNode;
import ua.abond.netty.game.util.function.Callable2;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PhysicsServiceImpl implements PhysicsService {
    private final Callable2<Collider, Collider> CALLABLE = new CollisionCallable();

    private SpatialIndex<Collider, QuadNode<Collider>> spatialIndex;
    private CollisionStrategyService collisionStrategyService;

    public PhysicsServiceImpl(SpatialIndex<Collider, QuadNode<Collider>> spatialIndex) {
        this.spatialIndex = spatialIndex;
        this.collisionStrategyService = new CollisionStrategyService();
    }

    @Override
    public void update(float delta) {
        spatialIndex.forEach(CALLABLE);
    }

    @Override
    public void add(Collider collider) {
        spatialIndex.add(collider);
    }

    @Override
    public void remove(Collider collider) {
        spatialIndex.remove(collider);
    }

    @Override
    public List<Collider> viewFrustum(Vector2 position, int width, int height) {
        Rect boundaries = new Rect(position.getX(), position.getY(), width, height);
        List<QuadNode<Collider>> colliders = new ArrayList<>();
        spatialIndex.query(boundaries, colliders);

        return colliders.stream()
                .map(QuadNode::getElement)
                .collect(Collectors.toList());
    }

    private final class CollisionCallable implements Callable2<Collider, Collider> {
        private final CollisionData collisionData = new CollisionData();

        @Override
        public void apply(Collider a, Collider b) {
            if (a.equals(b)) {
                return;
            }
            if (collisionStrategyService.checkCollision(a, b, collisionData)) {
                a.getCollidable().onCollision(b.getCollidable(), collisionData);
            }
            collisionData.reset();
        }
    }
}
