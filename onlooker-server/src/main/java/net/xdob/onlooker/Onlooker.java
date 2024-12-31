package net.xdob.onlooker;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.InetSocketAddress;
import java.nio.file.Paths;

public class Onlooker {
  static Logger LOG = LoggerFactory.getLogger(Onlooker.class);

  private UdpServerHandler udpServerHandler = null;
  private NioEventLoopGroup group = null;
  private Bootstrap bootstrap = null;
  public void start() {
    if(group==null) {
      udpServerHandler = new UdpServerHandler();
      group = new NioEventLoopGroup();
      bootstrap = new Bootstrap();
      bootstrap.group(group)
          .channel(NioDatagramChannel.class)
          .option(ChannelOption.SO_BROADCAST, true)
          .handler(udpServerHandler);
      bootstrap.bind(new InetSocketAddress(LookHelper.i.getServerPort()))
          .channel().closeFuture();
    }
  }

  public void stop(){
    if(group!=null) {
      group.shutdownGracefully();
      group = null;
      bootstrap = null;
      udpServerHandler = null;
    }
  }

  public static void initLogback(String configFilepathName) {
    File file = new File(configFilepathName);
    LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
    JoranConfigurator joranConfigurator = new JoranConfigurator();
    joranConfigurator.setContext(loggerContext);
    loggerContext.reset();
    try {
      joranConfigurator.doConfigure(file);
      LOG.info("initLogback ok");
    } catch (Exception e) {
      LOG.warn("initLogback", e);
      //System.out.println(String.format("Load logback config file error. Message: ", e.getMessage()));
    }
  }

}
