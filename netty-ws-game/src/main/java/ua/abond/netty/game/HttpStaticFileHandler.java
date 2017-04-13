package ua.abond.netty.game;

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

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.RandomAccessFile;
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
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class HttpStaticFileHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    public static final String HTTP_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";
    public static final String HTTP_DATE_GMT_TIMEZONE = "GMT";
    public static final int HTTP_CACHE_SECONDS = 60;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg)
            throws Exception {
        String uri = msg.uri();
        if ("/".equals(uri)) {
            uri = "index.html";
        }
        if (!msg.method().equals(HttpMethod.GET)) {
            ctx.channel().writeAndFlush(new DefaultHttpResponse(HTTP_1_1, HttpResponseStatus.FORBIDDEN))
                    .addListener(ChannelFutureListener.CLOSE);
            return;
        }
        URL resource = this.getClass().getClassLoader().getResource(uri);
        File in = null;
        if (resource != null) {
            in = new File(resource.toURI());
            if (!in.exists()) {
                ctx.channel().writeAndFlush(new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.NOT_FOUND))
                        .addListener(ChannelFutureListener.CLOSE);
                return;
            }
            HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
            setContentTypeHeader(response, in);
            setDateAndCacheHeaders(response, in);
            ctx.write(response);
            RandomAccessFile r = new RandomAccessFile(in, "r");
            ctx.writeAndFlush(new HttpChunkedInput(new ChunkedFile(r, 0, r.length(), 8192)))
                    .addListener(ChannelFutureListener.CLOSE);
            return;
        } else if (in != null) {
            if (!in.exists()) {
                ctx.channel().write(new DefaultHttpResponse(HTTP_1_1, HttpResponseStatus.NOT_FOUND))
                        .addListener(ChannelFutureListener.CLOSE);
                return;
            }
        }
        ctx.close();
    }

    private static void setDateAndCacheHeaders(HttpResponse response, File fileToCache) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
        dateFormatter.setTimeZone(TimeZone.getTimeZone(HTTP_DATE_GMT_TIMEZONE));

        // Date header
        Calendar time = new GregorianCalendar();
        response.headers().set(DATE, dateFormatter.format(time.getTime()));

        // Add cache headers
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
