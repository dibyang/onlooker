package net.xdob.onlooker.util;

import java.io.IOException;

public interface MetadataFile<T> {
  /**
   * 读取持久化文件中的元数据。
   * @return the metadata persisted in this file.
   */
  T getMetadata() throws IOException;

  /**
   * 将给定的元数据持久化到文件中。
   */
  void persist(T newMetadata) throws IOException;
}
