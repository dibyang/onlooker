package net.xdob.onlooker.util;


import net.xdob.onlooker.spi.DebugSupport;

import java.util.ServiceLoader;

public enum DebugHelper {
  helper;
  DebugSupport debugSupport = null;

  public boolean isDebug(String name){
    return getDebugSupport().isDebug(name);
  }

  private synchronized DebugSupport getDebugSupport() {
    if(debugSupport==null) {
      ServiceLoader<DebugSupport> load = ServiceLoader.load(DebugSupport.class);
      for (DebugSupport support : load) {
        debugSupport = support;
        break;
      }
    }
    if(debugSupport==null){
      debugSupport = new NoneDebugSupport();
    }
    return debugSupport;
  }

  public boolean isDebug(String name,String value){
    return getDebugSupport().isDebug(name,value);
  }


}
