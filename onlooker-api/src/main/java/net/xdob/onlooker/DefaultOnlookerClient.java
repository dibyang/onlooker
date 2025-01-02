package net.xdob.onlooker;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

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
    return setMessage(owner, messageToken, 0);
  }

  @Override
  public CompletableFuture<List<LookResponse>> getMessage(String owner){
    return getMessage(owner, 0);
  }

  @Override
  public CompletableFuture<List<LookResponse>> setMessage(String owner, MessageToken messageToken, int waitMS) {
    LookRequest request = LookRequest.newSet();
    request.setOwner(owner);
    request.setData(messageToken);
    return udpClientHandler.send(request, waitMS);
  }

  @Override
  public CompletableFuture<List<LookResponse>> getMessage(String owner, int waitMS) {
    LookRequest request = LookRequest.newGet();
    request.setOwner(owner);
    return udpClientHandler.send(request, waitMS);
  }

  @Override
  public CompletableFuture<List<MessageToken>> getMessageToken(String owner) {
    return getMessageToken(owner, 0);
  }

  @Override
  public CompletableFuture<List<MessageToken>> getMessageToken(String owner, int waitMS) {
    CompletableFuture<List<MessageToken>> future = new CompletableFuture<>();
    getMessage(owner, waitMS)
        .whenComplete((r,ex)->{
          if(ex!=null){
            future.completeExceptionally(ex);
          }else{
            List<MessageToken> messageTokenList = r.stream().filter(e -> e.getError() == null)
                .map(e -> e.getData(MessageToken.class))
                .collect(Collectors.toList());
            future.complete(messageTokenList);
          }
        });
    return future;
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
