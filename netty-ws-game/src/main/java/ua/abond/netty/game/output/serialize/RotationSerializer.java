package ua.abond.netty.game.output.serialize;

import io.netty.buffer.ByteBuf;
import ua.abond.netty.game.output.Serializer;
import ua.abond.netty.game.physics.Vector2;

public class RotationSerializer implements Serializer<Vector2> {

    @Override
    public ByteBuf serialize(Vector2 rotation, ByteBuf out) {
        int x = (int) (rotation.getX() * 10);
        x = x < 0 ? Math.abs(x) | (1 << 5) : x;

        int y = (int) (rotation.getY() * 10);
        y = y < 0 ? Math.abs(y) | (1 << 5) : y;

        out.writeByte(x);
        out.writeByte(y);
        return out;
    }
}
