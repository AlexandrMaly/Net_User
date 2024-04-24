package User;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.Scanner;

public class UserNet {
    static final String HOST = "127.0.0.1";
    static final int PORT = 8000;
    private static Scanner sc = new Scanner(System.in);
    private static String input;
    private static String name;

    public static void main(String[] args) throws Exception {

        System.out.println("Do you want to speak? Y/N");
        input = sc.nextLine();
        if (input.equalsIgnoreCase("Y")) {
            System.out.println("Please enter your name");
            name = sc.nextLine();
            System.out.println("Welcome " + name + ".\n" +
                    "Type your message to chat or type \"quit\" to exit.\n");
            chat(name);
        } else if (input.equalsIgnoreCase("N")) {
            System.out.println("Have a good day");
        } else {
            System.out.println("what do you want???");
        }
    }

    private static void chat(String name) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel socketChannel) {
                            ChannelPipeline channelPipeline = socketChannel.pipeline();
                            channelPipeline.addLast(new StringDecoder());
                            channelPipeline.addLast(new StringEncoder());
                            channelPipeline.addLast(new UserHandler());
                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect(HOST, PORT).sync();
            while (sc.hasNext()) {
                input = sc.nextLine();
                if (input.equals("quit")) System.exit(0);
                Channel channel = channelFuture.sync().channel();
                channel.writeAndFlush(name + " says " + input);
                channel.flush();
            }
            channelFuture.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

}
