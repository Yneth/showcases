package ua.abond.netty.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WebSocketIndexPageHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private final String websocketUri;

    public WebSocketIndexPageHandler(String websocketUri) {
        this.websocketUri = websocketUri;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        ByteBuf content = Unpooled.copiedBuffer(
                        "<html>\n" +
                        "<head>\n" +
                        "<title>\n" +
                        "Netty" +
                        "</title>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "<script>\n" +
                        "var ws = new WebSocket('ws://127.0.0.1:8082/websocket');\n" +
                        "ws.onmessage = function (e) {\n" +
                        "var paragraph = document.createElement('p');\n" +
                        "paragraph.appendChild(e.data);\n" +
                        "document.body.appendChild(paragraph);\n" +
                        "}\n" +
                        "function send() {\n" +
                        "var text = document.getElementById('message-input').value;\n" +
                        "ws.onmessage({'data': text});\n" +
                        "ws.send(text);\n" +
                        "}\n" +
                        "</script>\n" +
                        "<input id='message-input' type='text'/>\n" +
                        "<input type='button' onclick=send() value='Send'/>\n" +
                        "</body>\n" +
                        "</html>",
                CharsetUtil.US_ASCII
        );
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content
        );
        ctx.channel().writeAndFlush(response)
                .addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("Application thew an exception", cause);
        ctx.close();
    }
}
