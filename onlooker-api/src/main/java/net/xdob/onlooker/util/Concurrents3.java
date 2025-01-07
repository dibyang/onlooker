package net.xdob.onlooker.util;


import net.xdob.onlooker.util.function.CheckedFunction;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Utilities related to concurrent programming.
 */
public interface Concurrents3 {
  /**
   * 这个方法类似于 {@link AtomicReference#updateAndGet(java.util.function.UnaryOperator)}，
   * 但是它支持检查并处理可能抛出的检查型异常（THROWABLE）。
   * 它接受一个 AtomicReference<E> 和一个 CheckedFunction<E, E, THROWABLE> 类型的更新函数。
   * CheckedFunction 允许你提供一个可能抛出检查型异常的更新操作。
   * 如果更新函数抛出了异常，该异常将被捕获并重新抛出，确保操作的原子性，同时处理检查型异常。
   * 使用场景：适用于需要在更新引用时同时处理检查型异常的情况。
   */
  static <E, THROWABLE extends Throwable> E updateAndGet(AtomicReference<E> reference,
      CheckedFunction<E, E, THROWABLE> update) throws THROWABLE {
    final AtomicReference<Throwable> throwableRef = new AtomicReference<>();
    final E updated = reference.updateAndGet(value -> {
      try {
        return update.apply(value);
      } catch (Error | RuntimeException e) {
        throw e;
      } catch (Throwable t) {
        throwableRef.set(t);
        return value;
      }
    });
    @SuppressWarnings("unchecked")
    final THROWABLE t = (THROWABLE) throwableRef.get();
    if (t != null) {
      throw t;
    }
    return updated;
  }

}
