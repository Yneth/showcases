package ua.abond.netty.game.output.serialize;

import io.netty.buffer.ByteBuf;
import ua.abond.netty.game.domain.Player;
import ua.abond.netty.game.output.Serializer;
import ua.abond.netty.game.physics.Transform;
import ua.abond.netty.game.physics.Vector2;

public class PlayerSerializer implements Serializer<Player> {
    private final Serializer<Vector2> positionSerializer;
    private final Serializer<Vector2> rotationSerializer;

    public PlayerSerializer(Serializer<Vector2> positionSerializer,
                            Serializer<Vector2> rotationSerializer) {
        this.positionSerializer = positionSerializer;
        this.rotationSerializer = rotationSerializer;
    }

    @Override
    public ByteBuf serialize(Player player, ByteBuf out) {
        Transform transform = player.getTransform();
        positionSerializer.serialize(transform.getPosition(), out);
        rotationSerializer.serialize(transform.getRotation(), out);
        return out;
    }
}
