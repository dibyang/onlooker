package net.xdob.onlooker;

import com.ls.luava.common.Jsons;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import net.xdob.onlooker.exception.InvalidArgsException;
import net.xdob.onlooker.exception.OnlookerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class UdpServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {
  static final Logger LOG = LoggerFactory.getLogger(UdpServerHandler.class);

  private final MessageAdmin messageAdmin = new DefaultMessageAdmin();

  @Override
  protected void channelRead0(ChannelHandlerContext context, DatagramPacket msg) throws Exception {

    String message = msg.content().toString(CharsetUtil.UTF_8);
    LOG.info("received message：{} from {}", message, msg.sender().getHostString());
    LookRequest lookRequest = Jsons.i.fromJson(message, LookRequest.class);
    lookRequest.setSender(msg.sender().getHostString());
    if(LookRequestType.SET.equals(lookRequest.getLookRequestType())){
      LookResponse resp = LookResponse.c(lookRequest);
      resp.setOwner(lookRequest.getOwner());
      MessageToken messageToken = lookRequest.getData(MessageToken.class);
      if(messageToken!=null) {
        try {
          messageAdmin.setMessage(lookRequest.getOwner(), messageToken);
        }catch (OnlookerException e){
          resp.setError(e.getErrorCode());
          resp.setErrorMessage(e.getMessage());
        }
      }else{
        resp.setError(InvalidArgsException.INVALID_ARGS);
        resp.setErrorMessage("messageToken is null");
      }
      ByteBuf buf = toByteBuf(resp);
      context.writeAndFlush(new DatagramPacket(buf, msg.sender()));
    }else if(LookRequestType.GET.equals(lookRequest.getLookRequestType())){
      MessageToken messageToken = messageAdmin.getMessage(lookRequest.getOwner());
      LookResponse resp = LookResponse.c(lookRequest);
      if(messageToken!=null){
        resp.setData(messageToken);
      }
      ByteBuf buf = toByteBuf(resp);
      context.writeAndFlush(new DatagramPacket(buf, msg.sender()));
    }
  }

  private ByteBuf toByteBuf(LookMessage lookMessage) {
    String json = Jsons.i.toJson(lookMessage);
    return Unpooled.copiedBuffer(json.getBytes(StandardCharsets.UTF_8));
  }
}
