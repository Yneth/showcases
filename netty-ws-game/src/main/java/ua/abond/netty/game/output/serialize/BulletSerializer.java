package ua.abond.netty.game.output.serialize;

import io.netty.buffer.ByteBuf;
import ua.abond.netty.game.domain.Bullet;
import ua.abond.netty.game.output.Serializer;
import ua.abond.netty.game.physics.Vector2;

public class BulletSerializer implements Serializer<Bullet> {
    private final Serializer<Vector2> positionSerializer;

    public BulletSerializer(Serializer<Vector2> positionSerializer) {
        this.positionSerializer = positionSerializer;
    }

    @Override
    public ByteBuf serialize(Bullet bullet, ByteBuf out) {
        return positionSerializer.serialize(bullet.getTransform().getPosition(), out);
    }
}
