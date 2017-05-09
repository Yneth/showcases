package ua.abond.netty.game.output;

import io.netty.buffer.ByteBuf;
import ua.abond.netty.game.domain.Bullet;
import ua.abond.netty.game.domain.Player;
import ua.abond.netty.game.domain.Wall;
import ua.abond.netty.game.domain.component.CameraComponent;
import ua.abond.netty.game.output.serialize.BulletSerializer;
import ua.abond.netty.game.output.serialize.CameraSerializer;
import ua.abond.netty.game.output.serialize.PlayerSerializer;
import ua.abond.netty.game.output.serialize.PositionSerializer;
import ua.abond.netty.game.output.serialize.RotationSerializer;
import ua.abond.netty.game.output.serialize.WallSerializer;
import ua.abond.netty.game.physics.Vector2;

import java.util.HashMap;
import java.util.Map;

public class SerializationService {
    private Map<Class<?>, Serializer> serializers;

    public SerializationService() {
        this.serializers = new HashMap<>();

        Serializer<Vector2> positionSerializer = new PositionSerializer();
        Serializer<Vector2> rotationSerializer = new RotationSerializer();
        this.serializers.put(Wall.class, new WallSerializer(positionSerializer));
        this.serializers.put(Player.class, new PlayerSerializer(positionSerializer, rotationSerializer));
        this.serializers.put(Bullet.class, new BulletSerializer(positionSerializer));
        this.serializers.put(CameraComponent.class, new CameraSerializer(positionSerializer));
    }

    @SuppressWarnings("unchecked")
    public ByteBuf serialize(Object object, ByteBuf out) {
        return serializers.get(object.getClass())
                .serialize(object, out);
    }
}
