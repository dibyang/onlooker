package net.xdob.onlooker;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

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

  public static void main(String[] args) throws InterruptedException {
    OnlookerClient client = new DefaultOnlookerClient();
    client.start();
    MessageToken messageToken = new MessageToken();
    messageToken.setMessage("test2 is ok");
    messageToken.setSigner("evo4x");
    client.setMessage("evo4x",messageToken)
        .whenComplete((r,e)->{
          if(e==null){
            System.out.println("r = " + r);
          }
        });
    //Thread.sleep(1000);
    client.getMessage("evo4x")
        .whenComplete((r,e)->{
          if(e==null){
            System.out.println("r = " + r);
          }
          //client.stop();
        });
    //Thread.sleep(1000);
    client.getMessage("evo4x")
        .whenComplete((r,e)->{
          if(e==null){
            System.out.println("r = " + r);
          }
          client.stop();
        });
  }
}
