package net.xdob.onlooker;


import net.xdob.onlooker.util.FileDebugSupport;

import java.nio.file.Paths;

public class OnlookerDebugSupport extends FileDebugSupport {
  public OnlookerDebugSupport() {
    super(Paths.get("/etc/onlooker/config/debug").toFile());
  }
}
