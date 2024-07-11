package net.xdob.onlooker;

import com.google.common.base.Strings;
import com.ls.luava.common.Jsons;
import net.xdob.onlooker.exception.InvalidArgsException;
import net.xdob.onlooker.exception.InvalidSignException;
import net.xdob.onlooker.exception.OnlookerException;
import net.xdob.onlooker.exception.ReadErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DefaultMessageAdmin implements MessageAdmin{
  static final Logger LOG = LoggerFactory.getLogger(DefaultMessageAdmin.class);

  public static final String ETC_ONLOOKER_DATA = "/etc/onlooker/data/";
  private final SignAdmin signAdmin = new SignAdmin();

  @Override
  public void setMessage(String owner, MessageToken token) throws OnlookerException {
    if(!Strings.isNullOrEmpty(owner)&&token!=null) {
      if (signAdmin.verify(token.getSign(), token.getMessage(), token.getSign())) {
        Path ownerPath = Paths.get(ETC_ONLOOKER_DATA, owner);
        try {
          File parentFile = ownerPath.toFile().getParentFile();
          if(!parentFile.exists()){
            parentFile.mkdirs();
          }
          String json = Jsons.i.toJson(token);
          Files.write(ownerPath, json.getBytes(StandardCharsets.UTF_8));
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
    File file = Paths.get(ETC_ONLOOKER_DATA, owner).toFile();
    if(file.exists()){
      try {
        byte[] bytes = Files.readAllBytes(file.toPath());
        String json = new String(bytes, StandardCharsets.UTF_8);
        return Jsons.i.fromJson(json, MessageToken.class);
      } catch (Exception e){
        LOG.warn("getMessage fail", e);
      }
    }
    return null;
  }

}
