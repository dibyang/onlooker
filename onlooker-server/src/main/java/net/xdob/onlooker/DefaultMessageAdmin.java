package net.xdob.onlooker;

import com.google.common.base.Strings;
import net.xdob.onlooker.exception.InvalidArgsException;
import net.xdob.onlooker.exception.InvalidSignException;
import net.xdob.onlooker.exception.OnlookerException;
import net.xdob.onlooker.exception.ReadErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Properties;

public class DefaultMessageAdmin implements MessageAdmin{
  static final Logger LOG = LoggerFactory.getLogger(DefaultMessageAdmin.class);


  private final SignAdmin signAdmin = new SignAdmin();

  @Override
  public void setMessage(String owner, MessageToken token) throws OnlookerException {
    if(!Strings.isNullOrEmpty(owner)&&token!=null) {
      if (signAdmin.verify(token.getSigner(), token.getMessage(), token.getSign())) {
        Path ownerPath = Paths.get(LookHelper.i.getDataDir(), owner);
        try {
          File parentFile = ownerPath.toFile().getParentFile();
          if(!parentFile.exists()){
            parentFile.mkdirs();
          }
          Properties properties = new Properties();
          properties.putAll(token);
          try(OutputStream os = Files.newOutputStream(ownerPath, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)){
            properties.store(os,"");
          }
        } catch (Exception e){
          LOG.warn("setMessage", e);
          throw new ReadErrorException(owner);
        }
      }else{
        throw new InvalidSignException(owner);
      }
    }else{
      if(Strings.isNullOrEmpty(owner)) {
        throw new InvalidArgsException("owner is null or empty");
      }else if(token==null){
        throw new InvalidArgsException("token is null");
      }
    }
  }

  @Override
  public MessageToken getMessage(String owner) {
    File file = Paths.get(LookHelper.i.getDataDir(), owner).toFile();
    if(file.exists()){
      try {
        MessageToken token = new MessageToken();
        try(InputStream in = Files.newInputStream(file.toPath(), StandardOpenOption.READ)){
          Properties properties = new Properties();
          properties.load(in);
          for (Object key : properties.keySet()) {
            String name = key.toString();
            token.put(name, properties.getProperty(name));
          }
        }
        return token;
      } catch (Exception e){
        LOG.warn("getMessage fail", e);
      }
    }
    return null;
  }

}
