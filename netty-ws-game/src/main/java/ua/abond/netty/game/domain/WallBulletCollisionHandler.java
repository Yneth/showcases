package ua.abond.netty.game.domain;

public interface WallBulletCollisionHandler {
    void onCollision(Wall wall, Bullet bullet);
}
