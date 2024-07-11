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
import java.util.concurrent.atomic.AtomicReference;

public class UdpClientHandler extends SimpleChannelInboundHandler<DatagramPacket> {
  static final Logger LOG = LoggerFactory.getLogger(UdpClientHandler.class);

  private final Map<UUID, List<LookResponse>> responseMap = Maps.newConcurrentMap();


  private volatile CompletableFuture<ChannelFuture> channelFuture = null;

  private final NioEventLoopGroup group;
  private final Bootstrap bootstrap;
  private volatile int waitTime = 5;

  public UdpClientHandler(NioEventLoopGroup group, Bootstrap bootstrap) {
    this.group = group;
    this.bootstrap = bootstrap;
  }

  public int getWaitTime() {
    return waitTime;
  }

  public void setWaitTime(int waitTime) {
    this.waitTime = waitTime;
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

  private CompletableFuture<List<LookResponse>> postRequest(LookRequest request) {
    CompletableFuture<List<LookResponse>> completableFuture = new CompletableFuture<>();
    group.schedule(()->{
      List<LookResponse> allResponse = getAllResponse(request.getUid());
      if(allResponse!=null){
        completableFuture.complete(allResponse);
      }else{
        completableFuture.complete(Lists.newArrayList());
      }
    }, Math.max(waitTime, 2), TimeUnit.SECONDS);
    return completableFuture;
  }

  public synchronized CompletableFuture<List<LookResponse>> send(LookRequest request){
    newRequest(request.getUid());
    doSend(request, 0);
    return postRequest(request);
  }

  void doSend(LookRequest request, int failNum){
    if(failNum>3){
      return;
    }
    if(channelFuture==null) {
      channelFuture = new CompletableFuture<>();
      bootstrap.bind(0)
          .addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
              channelFuture.complete(future);
            }
          });
    }
    channelFuture.whenComplete((f,e)->{
      if (f.isSuccess()) {
        ByteBuf buf = toByteBuf(request);
        DatagramPacket packet = new DatagramPacket(buf, new InetSocketAddress("255.255.255.255", 6789));
        f.channel().writeAndFlush(packet);
      } else {
        doSend(request, failNum + 1);
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
