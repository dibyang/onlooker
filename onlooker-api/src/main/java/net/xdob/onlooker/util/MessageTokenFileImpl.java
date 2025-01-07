package net.xdob.onlooker.util;

import net.xdob.onlooker.MessageToken;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

public class MessageTokenFileImpl implements MetadataFile<MessageToken>{

  /**
   * 该字段表示用于存储 Raft 存储元数据的文件。
   */
  private final File file;
  /**
   * 使用 AtomicReference<RaftStorageMetadata> 来存储和操作元数据对象。
   * 通过 Concurrents3.updateAndGet 来确保元数据更新的原子性。
   */
  private final AtomicReference<MessageToken> metadata = new AtomicReference<>();

  public MessageTokenFileImpl(File file) {
    this.file = file;
  }

  /**
   * 获取存储的元数据。如果元数据尚未加载，它会从文件中加载元数据。
   * @return 存储的元数据
   */
  @Override
  public MessageToken getMetadata() throws IOException {
    return Concurrents3.updateAndGet(metadata, value -> value != null? value: load(file));
  }

  /**
   * 将新的元数据持久化到文件中。如果新元数据与当前存储的元数据不同，则进行原子写入操作。
   * @param newMetadata 新的元数据
   */
  @Override
  public void persist(MessageToken newMetadata) throws IOException {
    Concurrents3.updateAndGet(metadata,
        old -> Objects.equals(old, newMetadata)? old: atomicWrite(newMetadata, file));
  }

  @Override
  public String toString() {
    return "MessageTokenFileImpl:" + file;
  }

  /**
   * 原子地将 RaftStorageMetadata（包括 term 和 votedFor）写入指定文件。
   * 写入完成后，确保通过 fsync 将数据同步到磁盘。
   *
   * @throws IOException if the file cannot be written
   */
  static MessageToken atomicWrite(MessageToken metadata, File file) throws IOException {
    Properties properties = new Properties();
    properties.putAll(metadata);
    try(BufferedWriter out = new BufferedWriter(
        new OutputStreamWriter(new AtomicFileOutputStream(file), StandardCharsets.UTF_8))) {
      properties.store(out, "");
    }
    return metadata;
  }


  static MessageToken load(File file) throws IOException {
    if (!file.exists()) {
      return MessageToken.empty;
    }
    try(BufferedReader br = new BufferedReader(new InputStreamReader(
        FileUtils.newInputStream(file), StandardCharsets.UTF_8))) {
      Properties properties = new Properties();
      properties.load(br);
      MessageToken token = new MessageToken();
      for (Object key : properties.keySet()) {
        String name = key.toString();
        token.put(name, properties.getProperty(name));
      }
      return token;
    } catch (IOException e) {
      throw new IOException("Failed to load " + file, e);
    }
  }
}
