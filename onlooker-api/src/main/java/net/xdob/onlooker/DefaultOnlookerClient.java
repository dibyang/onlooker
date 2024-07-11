package net.xdob.onlooker;

import com.ls.luava.security.RSAUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DefaultOnlookerClient implements OnlookerClient {
  private  NioEventLoopGroup group;
  private  UdpClientHandler udpClientHandler;
  private  Bootstrap bootstrap;

  public DefaultOnlookerClient() {

  }

  @Override
  public void start() {
    group = new NioEventLoopGroup();
    bootstrap = new Bootstrap();
    udpClientHandler = new UdpClientHandler(group, bootstrap);
    bootstrap.group(group)
        .channel(NioDatagramChannel.class)
        .option(ChannelOption.SO_BROADCAST, true)
        .handler(udpClientHandler);

  }

  @Override
  public void stop() {
    if(group!=null){
      udpClientHandler = null;
      group.shutdownGracefully();
      group=null;
      bootstrap = null;
    }
  }





  @Override
  public CompletableFuture<List<LookResponse>> setMessage(String owner, MessageToken messageToken) {
    LookRequest request = LookRequest.newSet();
    request.setOwner(owner);
    request.setData(messageToken);
    return udpClientHandler.send(request);
  }



  @Override
  public CompletableFuture<List<LookResponse>> getMessage(String owner){
    LookRequest request = LookRequest.newGet();
    request.setOwner(owner);
    return udpClientHandler.send(request);
  }

  public static void main(String[] args)  {
    OnlookerClient client = new DefaultOnlookerClient();
    client.start();

    client.getMessage("evo4x")
        .whenComplete((r,e)->{
          if(e==null){
            System.out.println("r = " + r);
          }
          //client.stop();
        });
    client.getMessage("evo4x")
        .whenComplete((r,e)->{
          if(e==null){
            System.out.println("r = " + r);
          }
          client.stop();
        });
  }
}
