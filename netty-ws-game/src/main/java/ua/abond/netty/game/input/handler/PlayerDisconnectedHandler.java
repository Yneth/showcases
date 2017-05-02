package ua.abond.netty.game.input.handler;

import io.netty.channel.Channel;
import ua.abond.netty.game.ChannelMap;
import ua.abond.netty.game.domain.Player;
import ua.abond.netty.game.event.PlayerDisconnectedMessage;
import ua.abond.netty.game.input.MessageHandler;
import ua.abond.netty.game.physics.collision.PhysicsService;

public class PlayerDisconnectedHandler implements MessageHandler<PlayerDisconnectedMessage> {
    private final ChannelMap<Player> players;
    private final PhysicsService physicsService;

    public PlayerDisconnectedHandler(ChannelMap<Player> players, PhysicsService physicsService) {
        this.players = players;
        this.physicsService = physicsService;
    }

    @Override
    public void handle(PlayerDisconnectedMessage msg) {
        Channel channel = msg.getChannel();

        Player player = players.get(channel);
        if (player != null) {
            players.remove(channel);
            physicsService.remove(player.getCollider());
        }
    }
}
