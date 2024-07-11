package net.xdob.onlooker;

public class LookResponse extends LookMessage {

  public LookResponse() {
  }

  public LookResponse(LookRequest request) {
    super(request.getUid(), request.getOwner(), request.getLookRequestType());

  }


  public static LookResponse c(LookRequest request){
    return new LookResponse(request);
  }
}
