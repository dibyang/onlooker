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

  public static void main(String[] args) {
    String apphome = System.getProperty("app.home", System.getProperty("user.dir"));
    System.setProperty("app.home", apphome);

    String appName = System.getProperty("name", "onlooker");
    System.setProperty("app.name", appName);
    initLogback(Paths.get(apphome,"/config/logback.xml").toString());
    LOG.info("app home={}", apphome);
    LOG.info("app name={}", appName);
    Onlooker onlooker = new Onlooker();
    onlooker.start();
    Runtime.getRuntime().addShutdownHook(new Thread(){
      public void run(){
        onlooker.stop();
        LOG.info("{} is stopped", appName);
      }
    });
  }
}
