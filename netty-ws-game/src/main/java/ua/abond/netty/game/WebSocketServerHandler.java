package ua.abond.netty.game;

import java.security.SecureRandom;
import java.util.Random;
import java.util.stream.Collectors;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.concurrent.EventExecutor;
import lombok.extern.slf4j.Slf4j;
import ua.abond.netty.game.domain.Player;
import ua.abond.netty.game.domain.Vector2;

@Slf4j
public class WebSocketServerHandler extends SimpleChannelInboundHandler<WebSocketFrame> {
    private final Random random = new SecureRandom();
    private final ChannelMap<Player> playerMap;

    public WebSocketServerHandler(ChannelMap<Player> playerMap) {
        this.playerMap = playerMap;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        log.info("channelRegistered");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        Channel channel = ctx.channel();
        playerMap.remove(channel);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg)
            throws Exception {
//
//            executor.scheduleAtFixedRate(() -> {
//                final int maxHealthPacks = 5;
//                if (healthPacks.size() < maxHealthPacks) {
//                    healthPacks.add(HealthPack.builder().position(randomPosition()).build());
//                }
//            }, 0, 1000, TimeUnit.MILLISECONDS);
        if (msg instanceof PingWebSocketFrame) {
            log.warn("ping");
        }
        else if (msg instanceof TextWebSocketFrame) {
            String command = ((TextWebSocketFrame) msg).text();

            Channel channel = ctx.channel();
            if (command.startsWith("join:")) {
                String nickname = command.substring(5);
                Vector2 position = randomPosition();
                playerMap.put(channel, Player.builder()
                        .name(nickname)
                        .position(position)
                        .target(position)
                        .build()
                );
            } else if (command.startsWith("leave:")) {
                playerMap.remove(channel);
            } else if (command.startsWith("message:")) {
                String message = command.substring(8);
                playerMap.writeAndFlush(new TextWebSocketFrame(message));
            } else if (command.startsWith("0:")) {
                String[] positionPair = command.split(":")[1].split(",");
                Player currentPlayer = playerMap.get(channel);

                currentPlayer.setTarget(
                        Vector2.builder()
                                .x(Integer.parseInt(positionPair[0]))
                                .y(Integer.parseInt(positionPair[1]))
                                .build()
                );
                playerMap.put(channel, currentPlayer);

                playerMap.writeAndFlush(new TextWebSocketFrame(currentPlayer.toString()));
            }
        }
    }

    private Vector2 randomPosition() {
        return Vector2.builder().x(random.nextInt(500)).y(random.nextInt(500)).build();
    }
}
