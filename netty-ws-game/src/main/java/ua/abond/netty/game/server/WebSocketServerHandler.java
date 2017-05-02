package ua.abond.netty.game.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import ua.abond.netty.game.ChannelMap;
import ua.abond.netty.game.domain.Player;
import ua.abond.netty.game.event.Message;
import ua.abond.netty.game.event.PlayerAddedMessage;
import ua.abond.netty.game.event.PlayerDisconnectedMessage;
import ua.abond.netty.game.event.PlayerShootMessage;
import ua.abond.netty.game.input.MessageQueue;
import ua.abond.netty.game.physics.Vector2;

@Slf4j
public class WebSocketServerHandler extends SimpleChannelInboundHandler<WebSocketFrame> {
    private final ChannelMap<Player> playerMap;
    private final MessageQueue<Message> eventBus;

    public WebSocketServerHandler(ChannelMap<Player> playerMap, MessageQueue<Message> eventBus) {
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
        eventBus.push(new PlayerDisconnectedMessage(channel));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg)
            throws Exception {
        if (msg instanceof PingWebSocketFrame) {
            ctx.writeAndFlush(new PongWebSocketFrame());
        } else if (msg instanceof BinaryWebSocketFrame) {
            ByteBuf content = msg.content();
            int commandId = content.readByte();
            if (commandId == 0) {
                int x = content.readShort();
                int y = content.readShort();
                Player currentPlayer = playerMap.get(ctx.channel());

                if (currentPlayer == null) {
                    return;
                }
                currentPlayer.setDirection(
                        Vector2.builder()
                                .x(x)
                                .y(y)
                                .build()
                );
            } else if (commandId == 1) {
                eventBus.push(new PlayerShootMessage(ctx.channel()));
            } else if (commandId == 100) {
                byte[] array = new byte[21];
                content.readBytes(array);
                String nickname = new String(array, "UTF-8");
                eventBus.push(new PlayerAddedMessage(ctx.channel(), nickname));
            }
        }
    }
}
