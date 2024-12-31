package net.xdob.onlooker;

import com.ls.luava.common.Types2;

import java.nio.file.Paths;

public enum LookHelper {
  i;

  public static final int DEFAULT_SERVER_PORT = 1912;
  public static final int DEFAULT_CLIENT_PORT = 0;
  public static final int DEFAULT_WAIT_TIME = 200;
  /**
   * 服务器端口
   * @return 服务器端口
   */
  public int getServerPort(){
    int port = DEFAULT_SERVER_PORT;
    String lookPort = System.getProperty("look_server_port");
    if(lookPort!=null){
      port = Types2.cast(lookPort, Integer.class).orElse(DEFAULT_SERVER_PORT);
    }
    return port;
  }

  /**
   * 客户端端口（0表示任意可用端口）
   * @return 客户端端口
   */
  public int getClientPort(){
    int port = DEFAULT_CLIENT_PORT;
    String lookPort = System.getProperty("look_client_port");
    if(lookPort!=null){
      port = Types2.cast(lookPort, Integer.class).orElse(DEFAULT_CLIENT_PORT);
    }
    return port;
  }

  public int getWaitTime(){
    int waitTime = DEFAULT_WAIT_TIME;
    String wait_time = System.getProperty("look_wait_time");
    if(wait_time!=null){
      waitTime = Types2.cast(wait_time, Integer.class).orElse(DEFAULT_WAIT_TIME);
    }
    return waitTime;
  }

  public String getAppHome(){
    String app_home = System.getProperty("app.home", System.getProperty("user.dir"));
    System.setProperty("app.home", app_home);
    return app_home;
  }

  public String getPubDir(){
    return Paths.get(getAppHome(), "pub").toString();
  }
}
