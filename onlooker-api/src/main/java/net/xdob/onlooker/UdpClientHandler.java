package net.xdob.onlooker;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ls.luava.common.Jsons;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class UdpClientHandler extends SimpleChannelInboundHandler<DatagramPacket> {
  static final Logger LOG = LoggerFactory.getLogger(UdpClientHandler.class);
  public static final String ANY_ADDRESS = "255.255.255.255";

  private final Map<UUID, List<LookResponse>> responseMap = Maps.newConcurrentMap();


  private volatile CompletableFuture<ChannelFuture> channelFuture = null;

  private final NioEventLoopGroup group;
  private final Bootstrap bootstrap;


  public UdpClientHandler(NioEventLoopGroup group, Bootstrap bootstrap) {
    this.group = group;
    this.bootstrap = bootstrap;
  }


  @Override
  protected void channelRead0(ChannelHandlerContext context, DatagramPacket msg) throws Exception {
    String message = msg.content().toString(CharsetUtil.UTF_8);
    LOG.info("received message：{} from {}", message, msg.sender().getHostString());
    LookResponse response = Jsons.i.fromJson(message, LookResponse.class);
    response.setSender(msg.sender().getHostString());
    List<LookResponse> list = responseMap.get(response.getUid());
    if(list!=null){
      list.add(response);
    }
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    super.exceptionCaught(ctx, cause);
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    channelFuture = null;
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    super.channelActive(ctx);
  }

  private CompletableFuture<List<LookResponse>> postRequest(LookRequest request, int waitMS) {
    CompletableFuture<List<LookResponse>> completableFuture = new CompletableFuture<>();
    group.schedule(()->{
      List<LookResponse> allResponse = getAllResponse(request.getUid());
      if(allResponse!=null){
        completableFuture.complete(allResponse);
      }else{
        completableFuture.complete(Lists.newArrayList());
      }
    }, Math.max(Math.max(LookHelper.i.getWaitTime(), 10), waitMS), TimeUnit.MILLISECONDS);
    return completableFuture;
  }

  public synchronized CompletableFuture<List<LookResponse>> send(LookRequest request, int waitMS){
    newRequest(request.getUid());
    ByteBuf buf = toByteBuf(request);

    DatagramPacket packet = new DatagramPacket(buf, new InetSocketAddress(ANY_ADDRESS, LookHelper.i.getServerPort()));
    doSend(packet, 0);
    return postRequest(request, waitMS);
  }

  void doSend( DatagramPacket packet, int failNum){
    if(failNum>3){
      return;
    }
    if(channelFuture==null) {
      channelFuture = new CompletableFuture<>();
      bootstrap.bind(LookHelper.i.getClientPort())
          .addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
              channelFuture.complete(future);
            }
          });
    }
    channelFuture.whenComplete((f,e)->{
      if (f.isSuccess()) {
        f.channel().writeAndFlush(packet);
      } else {
        doSend(packet, failNum + 1);
      }
    });
  }

  private ByteBuf toByteBuf(LookRequest lookMessage) {
    String json = Jsons.i.toJson(lookMessage);
    return Unpooled.copiedBuffer(json.getBytes(StandardCharsets.UTF_8));
  }


  void newRequest(UUID uid){
    responseMap.put(uid, Lists.newArrayList());
  }

  List<LookResponse> getAllResponse(UUID uid){
    return responseMap.remove(uid);
  }

}
