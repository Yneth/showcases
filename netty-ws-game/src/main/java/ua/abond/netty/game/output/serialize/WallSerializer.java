package ua.abond.netty.game.output.serialize;

import io.netty.buffer.ByteBuf;
import ua.abond.netty.game.domain.Wall;
import ua.abond.netty.game.output.Serializer;
import ua.abond.netty.game.physics.Vector2;

public class WallSerializer implements Serializer<Wall> {
    private final Serializer<Vector2> positionSerializer;

    public WallSerializer(Serializer<Vector2> positionSerializer) {
        this.positionSerializer = positionSerializer;
    }

    @Override
    public ByteBuf serialize(Wall wall, ByteBuf out) {
        positionSerializer.serialize(wall.getTransform().getPosition(), out);
        out.writeShort(wall.getCollider().width() * 10);
        out.writeShort(wall.getCollider().height() * 10);
        return out;
    }
}
