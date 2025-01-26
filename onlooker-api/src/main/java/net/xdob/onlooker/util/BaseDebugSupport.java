package net.xdob.onlooker.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.xdob.onlooker.spi.DebugSupport;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class BaseDebugSupport implements DebugSupport {
  private final Map<String, Set<String>> map = Maps.newConcurrentMap();


  @Override
  public synchronized void removeAll(String name) {
    map.remove(name);
  }

  @Override
  public synchronized void removeValue(String name, String value) {
    Set<String> values = map.get(name);
    if (values != null) {
      values.remove(value);
    }
  }

  @Override
  public synchronized void clear() {
    map.clear();
  }

  @Override
  public void addValue(String name, String... values) {
    addValue(name, Lists.newArrayList(values));
  }

  @Override
  public synchronized void addValue(String name, Collection<String> values) {
    for (String value : values) {
      Set<String> set = map.get(name);
      if (set == null) {
        set = Sets.newHashSet();
        map.put(name, set);
      }
      set.add(value);
    }
  }

  public boolean isDebug(String name) {
    Set<String> values = getValues(name);
    return values!=null;
  }

  public boolean isDebug(String name, String value) {
    Set<String> values = getValues(name);
    if(values != null){
      for (String p : values) {
        if(StringMatcher.isMatch(value,p)){
          return true;
        }
      }
    }
    return false;
  }

  protected Set<String> getValues(String name){
    reload();
    Set<String> values = map.get(name);;
    return values;
  }

  /**
   * 重新加载配置
   */
  protected synchronized void reload(){

  }

  public static void main(String[] args) {
    DebugSupport debugSupport = new BaseDebugSupport();
    debugSupport.addValue("test", "");
    debugSupport.addValue("test","13.13.*");
    debugSupport.addValue("test","10.1.0.20");
    boolean test = debugSupport.isDebug("test");
    System.out.println("test = " + test);
    boolean test1 = debugSupport.isDebug("test","10.1.0.25");
    System.out.println("test1 = " + test1);
    boolean test2 = debugSupport.isDebug("test","13.13.0.25");
    System.out.println("test2 = " + test2);
    boolean test3 = debugSupport.isDebug("test","10.1.0.20");
    System.out.println("test3 = " + test3);
  }

}
