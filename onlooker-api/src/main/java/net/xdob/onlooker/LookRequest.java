package net.xdob.onlooker;

public class LookRequest extends LookMessage{

  public LookRequest() {
  }

  public LookRequest(LookRequestType lookRequestType) {
    super(lookRequestType);
  }

  public static LookRequest newGet(){
    return new LookRequest(LookRequestType.GET);
  }

  public static LookRequest newSet(){
    return new LookRequest(LookRequestType.SET);
  }

}
