package net.xdob.onlooker.spi;

import java.util.Collection;

public interface DebugSupport {
  boolean isDebug(String name);
  boolean isDebug(String name, String value);
  void removeAll(String name);
  void removeValue(String name, String value);
  void clear();
  void addValue(String name, String... value);
  void addValue(String name, Collection<String> values);
}
