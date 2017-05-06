package ua.abond.netty.game.physics.collision.service;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ua.abond.netty.game.physics.collision.Collider;
import ua.abond.netty.game.physics.collision.CollisionData;
import ua.abond.netty.game.physics.collision.CollisionService;
import ua.abond.netty.game.physics.collision.collider.CircleCollider;
import ua.abond.netty.game.physics.collision.collider.RectCollider;
import ua.abond.netty.game.physics.collision.strategy.CircleCircleCollisionStrategy;
import ua.abond.netty.game.physics.collision.strategy.CircleRectCollisionStrategy;
import ua.abond.netty.game.physics.collision.strategy.RectRectCollisionStrategy;

import java.util.HashMap;
import java.util.Map;

public class CollisionStrategyService {
    private final Map<Key, CollisionService> strategies;

    private final Key cachedKey = new Key();

    public CollisionStrategyService() {
        this.strategies = new HashMap<>();
        this.strategies.put(new Key(CircleCollider.class, CircleCollider.class), new CircleCircleCollisionStrategy());
        this.strategies.put(new Key(CircleCollider.class, RectCollider.class), new CircleRectCollisionStrategy());
        this.strategies.put(new Key(RectCollider.class, RectCollider.class), new RectRectCollisionStrategy());
    }

    @SuppressWarnings("unchecked")
    public boolean checkCollision(Collider c0, Collider c1, CollisionData data) {
        cachedKey.set(c0, c1);
        if (cachedKey.swapped) {
            Collider temp = c0;
            c0 = c1;
            c1 = temp;
        }
        return strategies
                .get(cachedKey)
                .collides(c0, c1, data);
    }

    @NoArgsConstructor
    @EqualsAndHashCode(of = {"left", "right"})
    private final static class Key {
        Class<? extends Collider> left;
        Class<? extends Collider> right;

        boolean swapped;

        private Key(Class<? extends Collider> c0, Class<? extends Collider> c1) {
            this.set(c0, c1);
        }

        public final Key set(Collider c0, Collider c1) {
            return set(c0.getClass(), c1.getClass());
        }

        public final Key set(Class<? extends Collider> c0, Class<? extends Collider> c1) {
            if (c0.hashCode() > c1.hashCode()) {
                left = c1;
                right = c0;
                swapped = true;
            } else {
                left = c0;
                right = c1;
                swapped = false;
            }
            return this;
        }
    }
}
