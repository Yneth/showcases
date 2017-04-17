package ua.abond.netty.game.domain;

@FunctionalInterface
public interface BulletCollisionHandler {

    void onCollision(Player player, Bullet bullet);
}
