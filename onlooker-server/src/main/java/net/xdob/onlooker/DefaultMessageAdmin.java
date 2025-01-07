package net.xdob.onlooker;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import net.xdob.onlooker.exception.InvalidArgsException;
import net.xdob.onlooker.exception.InvalidSignException;
import net.xdob.onlooker.exception.OnlookerException;
import net.xdob.onlooker.exception.ReadErrorException;
import net.xdob.onlooker.util.MessageTokenFileImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class DefaultMessageAdmin implements MessageAdmin{
  static final Logger LOG = LoggerFactory.getLogger(DefaultMessageAdmin.class);


  private final SignAdmin signAdmin = new SignAdmin();
  private final Map<String, MessageTokenFileImpl> owners = Maps.newConcurrentMap();

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
          MessageTokenFileImpl termIndexFile = owners.computeIfAbsent(owner, k->new MessageTokenFileImpl(ownerPath.toFile()));

          termIndexFile.persist(token);
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
        MessageTokenFileImpl termIndexFile = owners.computeIfAbsent(owner, k->new MessageTokenFileImpl(file));
        return termIndexFile.getMetadata();
      } catch (Exception e){
        LOG.warn("getMessage fail", e);
      }
    }
    return null;
  }

}
