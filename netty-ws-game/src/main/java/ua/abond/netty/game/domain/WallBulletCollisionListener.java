package ua.abond.netty.game.domain;

public interface WallBulletCollisionListener {
    void onCollision(WallBehaviour wall, Bullet bullet);
}
