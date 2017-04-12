package ua.abond.netty.game;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.concurrent.EventExecutor;
import lombok.extern.slf4j.Slf4j;
import ua.abond.netty.game.domain.Bullet;
import ua.abond.netty.game.domain.HealthPack;
import ua.abond.netty.game.domain.Player;
import ua.abond.netty.game.domain.Vector2;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
public class WebSocketServerHandler extends SimpleChannelInboundHandler<WebSocketFrame> {
    private final Random random = new SecureRandom();
    private final ChannelGroup channelGroup;

    private final List<Bullet> bullets = new ArrayList<>();
    private final List<HealthPack> healthPacks = new ArrayList<>();

    private final Map<ChannelId, Player> users = new HashMap<>();

    private boolean firstRun = true;

    public WebSocketServerHandler(ChannelGroup channelGroup) {
        this.channelGroup = channelGroup;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        log.info("channelRegistered");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        Channel channel = ctx.channel();
        channelGroup.remove(channel);
        users.remove(channel.id());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg)
            throws Exception {
        if (firstRun) {
            EventExecutor executor = ctx.executor();

            executor.scheduleAtFixedRate(() -> {
                final float deltaTime = 17 / 1000f;
                final float speed = 1;
                for (Player player : users.values()) {
                    player.getPosition().add(player.getTarget().normalize().multiply(speed).multiply(deltaTime));
                    channelGroup.writeAndFlush(new TextWebSocketFrame(player.getPosition().toString()));
                }
            }, 0, 17, TimeUnit.MILLISECONDS);

            executor.scheduleAtFixedRate(() -> {
                final int maxHealthPacks = 5;
                if (healthPacks.size() < maxHealthPacks) {
                    healthPacks.add(HealthPack.builder().position(randomPosition()).build());
                }
            }, 0, 1000, TimeUnit.MILLISECONDS);

            executor.scheduleAtFixedRate(() -> {
                channelGroup.writeAndFlush(new TextWebSocketFrame(healthPacks.toString() + "," + users.values().toString()));
            }, 0, 33, TimeUnit.MILLISECONDS);
            firstRun = false;
        }
        if (msg instanceof PingWebSocketFrame) {
            log.warn("ping");
        }
        if (msg instanceof TextWebSocketFrame) {
            String command = ((TextWebSocketFrame) msg).text();

            Channel channel = ctx.channel();
            if (command.startsWith("join:")) {
                String nickname = command.substring(5);
                channelGroup.add(channel);
                users.put(channel.id(),
                        Player.builder()
                                .name(nickname)
                                .position(randomPosition())
                                .target(Vector2.ZERO)
                                .build()
                );
            } else if (command.startsWith("leave:")) {
                channelGroup.remove(channel);
                users.remove(channel.id());
            } else if (command.startsWith("message:")) {
                String message = command.substring(8);
                channelGroup.writeAndFlush(new TextWebSocketFrame(message));
            } else if (command.startsWith("0:")) {
                String[] positionPair = command.split(":")[1].split(",");
                Player currentPlayer = users.get(channel.id());

                currentPlayer.setTarget(
                        Vector2.builder()
                                .x(Integer.parseInt(positionPair[0]))
                                .y(Integer.parseInt(positionPair[1]))
                                .build()
                );

                channelGroup.writeAndFlush(new TextWebSocketFrame(currentPlayer.toString()));
            }
        }
    }

    private Vector2 randomPosition() {
        return Vector2.builder().x(random.nextInt(10)).y(random.nextInt(10)).build();
    }
}
