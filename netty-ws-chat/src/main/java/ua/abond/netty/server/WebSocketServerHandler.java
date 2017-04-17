package ua.abond.netty.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WebSocketServerHandler extends SimpleChannelInboundHandler<WebSocketFrame> {
    private final ChannelGroup channelGroup;

    public WebSocketServerHandler(ChannelGroup channelGroup) {
        this.channelGroup = channelGroup;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        channelGroup.add(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        channelGroup.remove(ctx.channel());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg) throws Exception {
        if (msg instanceof PingWebSocketFrame) {
            log.info("ping");
        } else if (msg instanceof PongWebSocketFrame) {
            log.info("pong");
        } else if (msg instanceof TextWebSocketFrame) {
            log.info("text");
            String text = ((TextWebSocketFrame) msg).text();

            for (Channel channel : channelGroup) {
                if (!channel.equals(ctx.channel())) {
                    channel.writeAndFlush(new TextWebSocketFrame(text));
                }
            }
        } else if (msg instanceof BinaryWebSocketFrame) {
            log.info("bin");
        } else if (msg instanceof ContinuationWebSocketFrame) {
            log.info("cont");
        } else if (msg instanceof CloseWebSocketFrame) {
            log.info("close");
        }
    }
}
