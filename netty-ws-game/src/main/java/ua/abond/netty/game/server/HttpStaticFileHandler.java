package ua.abond.netty.game.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpChunkedInput;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.stream.ChunkedFile;
import lombok.extern.slf4j.Slf4j;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import static io.netty.handler.codec.http.HttpHeaderNames.CACHE_CONTROL;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaderNames.DATE;
import static io.netty.handler.codec.http.HttpHeaderNames.EXPIRES;
import static io.netty.handler.codec.http.HttpHeaderNames.LAST_MODIFIED;
import static io.netty.handler.codec.http.HttpHeaderValues.CHUNKED;
import static io.netty.handler.codec.http.HttpHeaders.Names.TRANSFER_ENCODING;
import static io.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

@Slf4j
public class HttpStaticFileHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private static final int HTTP_CACHE_SECONDS = 60;
    private static final String HTTP_DATE_GMT_TIMEZONE = "GMT";
    private static final String HTTP_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg)
            throws Exception {
        handle(ctx, msg)
                .addListener(ChannelFutureListener.CLOSE);
    }

    private ChannelFuture handle(ChannelHandlerContext ctx, FullHttpRequest msg) {
        Channel channel = ctx.channel();
        String uri = msg.uri();
        if ("/".equals(uri)) {
            uri = "/index.html";
        }
        if (uri.startsWith("/ua")) {
            return sendError(channel, NOT_FOUND);
        }
        if (!msg.method().equals(HttpMethod.GET)) {
            return sendError(channel, FORBIDDEN);
        }

        try {
            URL resource = this.getClass().getResource(uri);
            if (resource != null) {
                File in = in = new File(resource.toURI());

                HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
                setContentTypeHeader(response, in);
//            setDateAndCacheHeaders(response, in);
                response.headers().set(TRANSFER_ENCODING, CHUNKED);

                ctx.write(response);
                return ctx.writeAndFlush(new HttpChunkedInput(new ChunkedFile(in)));
            } else {
                return sendError(channel, NOT_FOUND);
            }
        } catch (Exception e) {
            log.error("failed to execute.", e);
            return sendError(channel, NOT_FOUND);
        }
    }

    private ChannelFuture sendError(Channel channel, HttpResponseStatus status) {
        return channel.writeAndFlush(new DefaultFullHttpResponse(HTTP_1_1, status));
    }

    private static void setDateAndCacheHeaders(HttpResponse response, File fileToCache) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
        dateFormatter.setTimeZone(TimeZone.getTimeZone(HTTP_DATE_GMT_TIMEZONE));

        Calendar time = new GregorianCalendar();
        response.headers().set(DATE, dateFormatter.format(time.getTime()));

        time.add(Calendar.SECOND, HTTP_CACHE_SECONDS);
        response.headers().set(EXPIRES, dateFormatter.format(time.getTime()));
        response.headers().set(CACHE_CONTROL, "private, max-age=" + HTTP_CACHE_SECONDS);
        response.headers().set(
                LAST_MODIFIED, dateFormatter.format(new Date(fileToCache.lastModified())));
    }

    private static void setContentTypeHeader(HttpResponse response, File file) {
        MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
        response.headers().set(CONTENT_TYPE, mimeTypesMap.getContentType(file.getPath()));
    }
}
