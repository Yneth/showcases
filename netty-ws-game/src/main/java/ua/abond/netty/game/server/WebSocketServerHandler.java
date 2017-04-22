package ua.abond.netty.game.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import ua.abond.netty.game.ChannelMap;
import ua.abond.netty.game.domain.Player;
import ua.abond.netty.game.event.Message;
import ua.abond.netty.game.event.PlayerAddedMessage;
import ua.abond.netty.game.event.PlayerDisconnectedMessage;
import ua.abond.netty.game.event.PlayerShootMessage;
import ua.abond.netty.game.physics.Vector2;

import java.util.Queue;

@Slf4j
public class WebSocketServerHandler extends SimpleChannelInboundHandler<WebSocketFrame> {
    private final ChannelMap<Player> playerMap;
    private final Queue<Message> eventBus;

    public WebSocketServerHandler(ChannelMap<Player> playerMap, Queue<Message> eventBus) {
        this.playerMap = playerMap;
        this.eventBus = eventBus;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        log.info("channelRegistered");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        Channel channel = ctx.channel();
        eventBus.add(new PlayerDisconnectedMessage(channel));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg)
            throws Exception {
        if (msg instanceof PingWebSocketFrame) {
            ctx.writeAndFlush(new PongWebSocketFrame());
        } else if (msg instanceof TextWebSocketFrame) {
            String command = ((TextWebSocketFrame) msg).text();
            Channel channel = ctx.channel();
            if (command.startsWith("join:")) {
                String nickname = command.substring(5);
                eventBus.add(new PlayerAddedMessage(channel, nickname));
            } else if (command.startsWith("leave:")) {
                playerMap.remove(channel);
            } else if (command.startsWith("message:")) {
                String message = command.substring(8);
                playerMap.writeAndFlush(new TextWebSocketFrame(message));
            } else if (command.startsWith("0:")) {
                String[] positionPair = command.split(":")[1].split(",");
                Player currentPlayer = playerMap.get(channel);

                if (currentPlayer == null) {
                    return;
                }
                currentPlayer.setTarget(
                        Vector2.builder()
                                .x(Integer.parseInt(positionPair[0]))
                                .y(Integer.parseInt(positionPair[1]))
                                .build()
                );
                playerMap.put(channel, currentPlayer);
            } else if (command.startsWith("1:")) {
                eventBus.add(new PlayerShootMessage(channel));
            }
        } else if (msg instanceof BinaryWebSocketFrame) {
            ByteBuf content = msg.content();
            int commandId = content.readByte();
            switch (commandId) {
                case 0: {
                    int x = content.readShort();
                    int y = content.readShort();
                    Player currentPlayer = playerMap.get(ctx.channel());

                    if (currentPlayer == null) {
                        return;
                    }
                    currentPlayer.setTarget(
                            Vector2.builder()
                                    .x(x)
                                    .y(y)
                                    .build()
                    );
                    break;
                }
                case 1: {
                    eventBus.add(new PlayerShootMessage(ctx.channel()));
                }
                case 100: {
                    byte[] array = new byte[21];
                    content.readBytes(array);
                    String nickname = new String(array, "UTF-8");
                    eventBus.add(new PlayerAddedMessage(ctx.channel(), nickname));
                    break;
                }
            }
        }
    }
}
