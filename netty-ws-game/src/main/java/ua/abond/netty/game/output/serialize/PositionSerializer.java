package ua.abond.netty.game.output.serialize;

import io.netty.buffer.ByteBuf;
import ua.abond.netty.game.output.Serializer;
import ua.abond.netty.game.physics.Vector2;

public class PositionSerializer implements Serializer<Vector2> {

    @Override
    public ByteBuf serialize(Vector2 position, ByteBuf out) {
        out.writeShort((int) (position.getX() * 10));
        out.writeShort((int) (position.getY() * 10));
        return out;
    }
}
