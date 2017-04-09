package ua.abond.netty.game;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.internal.PlatformDependent;
import lombok.extern.slf4j.Slf4j;
import ua.abond.netty.game.domain.Player;
import ua.abond.netty.game.domain.Vector2;

import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@ChannelHandler.Sharable
public class WebSocketServerHandler extends SimpleChannelInboundHandler<WebSocketFrame> {
    private final Random random = new SecureRandom();
    private final ChannelGroup channelGroup;
    private final ConcurrentMap<ChannelId, Player> users;

    public WebSocketServerHandler(ChannelGroup channelGroup) {
        this.users = PlatformDependent.newConcurrentHashMap();
        this.channelGroup = channelGroup;
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
        if (msg instanceof TextWebSocketFrame) {
            String command = ((TextWebSocketFrame) msg).text();

            Channel channel = ctx.channel();
            if (command.startsWith("join:")) {
                String nickname = command.substring(5);
                channelGroup.add(channel);
                users.put(channel.id(),
                        Player.builder()
                                .name(nickname)
                                .position(Vector2.builder().x(random.nextInt(10)).y(random.nextInt(10)).build())
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

                currentPlayer.setPosition(
                        Vector2.builder()
                                .x(Integer.parseInt(positionPair[0]))
                                .y(Integer.parseInt(positionPair[1]))
                                .build()
                );

                channelGroup.writeAndFlush(new TextWebSocketFrame(currentPlayer.toString()));
            }
        }
    }
}
