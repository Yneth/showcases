package ua.abond.netty.game.output.serialize;

import io.netty.buffer.ByteBuf;
import ua.abond.netty.game.domain.component.CameraComponent;
import ua.abond.netty.game.output.Serializer;
import ua.abond.netty.game.physics.Vector2;

public class CameraSerializer implements Serializer<CameraComponent> {
    private final Serializer<Vector2> positionSerializer;

    public CameraSerializer(Serializer<Vector2> positionSerializer) {
        this.positionSerializer = positionSerializer;
    }

    @Override
    public ByteBuf serialize(CameraComponent cameraComponent, ByteBuf out) {
        return positionSerializer.serialize(cameraComponent.getPosition(), out);
    }
}
