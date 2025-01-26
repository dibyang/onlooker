package net.xdob.onlooker.util;


import java.util.Optional;

/**
 * @author yangzj
 */
public abstract class Types2 {

  public static <T> Optional<T> cast(Object value, Class<T> targetClass, NameMapping mapping) {
    try {
      return Optional.ofNullable(Types.cast(value,targetClass,mapping));
    } catch (CastException e) {
      //
    }
    return Optional.empty();
  }


  public static <T> Optional<T> cast(Object value, Class<T> targetClass) {
    return cast(value,targetClass,null);
  }


}
