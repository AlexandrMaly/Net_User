package serverNet;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.ArrayList;
import java.util.List;

@ChannelHandler.Sharable
public class ServerHandler extends SimpleChannelInboundHandler<String> {
    static final List<Channel> channels = new ArrayList<>();

    @Override
    public void channelActive(final ChannelHandlerContext channelHandlerContext) {
        System.out.println("User joined " + channelHandlerContext);
        channels.add(channelHandlerContext.channel());
    }

    @Override
    public void channelRead0(ChannelHandlerContext channelHandlerContext, String message) {
        if (message.equalsIgnoreCase("quit")) {
            System.out.println("closing connection for - " + channelHandlerContext);
            for (Channel c : channels) {
                c.writeAndFlush(message);
            }
            channelHandlerContext.close();
        } else {
            System.out.println("User " + message);
            for (Channel c : channels) {
                c.writeAndFlush(message + '\n');
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause) {
        System.out.println("Closing connection for user - " + channelHandlerContext);
        channelHandlerContext.close();
    }
}
