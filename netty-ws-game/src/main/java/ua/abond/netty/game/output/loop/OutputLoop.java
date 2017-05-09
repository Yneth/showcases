package ua.abond.netty.game.output.loop;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import ua.abond.netty.game.ChannelMap;
import ua.abond.netty.game.domain.Bullet;
import ua.abond.netty.game.domain.Player;
import ua.abond.netty.game.domain.Wall;
import ua.abond.netty.game.domain.component.CameraComponent;
import ua.abond.netty.game.output.OutputCallback;
import ua.abond.netty.game.output.SerializationService;
import ua.abond.netty.game.physics.collision.Collidable;
import ua.abond.netty.game.physics.collision.Collider;
import ua.abond.netty.game.physics.collision.PhysicsService;

import java.util.List;

public class OutputLoop implements OutputCallback {
    private final ChannelMap<Player> players;
    private final PhysicsService physicsService;
    private final SerializationService serializationService;

    public OutputLoop(ChannelMap<Player> players, PhysicsService physicsService) {
        this.players = players;
        this.physicsService = physicsService;
        this.serializationService = new SerializationService();
    }

    @Override
    public void call(ByteBufAllocator allocator) {
        for (Player player : players.values()) {
            Channel channel = players.find(players.find(player));
            if (channel == null)
                return;

            CameraComponent cameraComponent = player.getCameraComponent();
            List<Collider> colliders = physicsService.viewFrustum(
                    cameraComponent.getPosition(),
                    cameraComponent.getWidth(), cameraComponent.getHeight()
            );
            if (cameraComponent.isUpdated()) {
                ByteBuf buf = allocator.directBuffer();
                buf.writeByte(0);
                serializationService.serialize(cameraComponent, buf);
                writeIfNotEmpty(channel, buf);
                cameraComponent.setUpdated(false);
            }

            ByteBuf playerBuffer = allocator.directBuffer();
            playerBuffer.writeByte(1);
            ByteBuf bulletBuffer = allocator.directBuffer();
            bulletBuffer.writeByte(2);
            ByteBuf wallBuffer = allocator.directBuffer();
            wallBuffer.writeByte(3);
            for (Collider collider : colliders) {
                Collidable collidable = collider.getCollidable();
                if (collidable instanceof Player) {
                    serializationService.serialize(collidable, playerBuffer);
                } else if (collidable instanceof Bullet) {
                    serializationService.serialize(collidable, bulletBuffer);
                } else if (collidable instanceof Wall) {
                    serializationService.serialize(collidable, wallBuffer);
                }
            }
            writeIfNotEmpty(channel, playerBuffer);
            writeIfNotEmpty(channel, bulletBuffer);
            writeIfNotEmpty(channel, wallBuffer);
        }
        players.flush();
    }

    private void writeIfNotEmpty(Channel channel, ByteBuf msg) {
        if (msg.writerIndex() != 1) {
            channel.write(new BinaryWebSocketFrame(msg), channel.voidPromise());
        } else {
            msg.release();
        }
    }
}
