package ua.abond.netty.game.domain;

import ua.abond.netty.game.physics.Vector2;

public class WallFactory {

    public GameObject createWall(Vector2 position, WallBulletCollisionListener listener) {
        GameObject gameObject = new GameObject();

        gameObject.getTransform().setPosition(position);
        gameObject.addComponent(new WallBehaviour(gameObject));

        WallCollider collider = new WallCollider(gameObject);
        collider.setWallBulletCollisionListener(listener);
        gameObject.addComponent(collider);

        return gameObject;
    }
}
