package net.xdob.onlooker;

import com.ls.luava.security.RSAUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SignAdmin {
  public static final String ETC_ONLOOKER_PUB = "/etc/onlooker/pub/";
  static Logger LOG = LoggerFactory.getLogger(SignAdmin.class);

  private String getPubicKey(String signer){
    if("evo4x".equals(signer)){
      return getKey("evo4x.pub");
    }else{
      File file = Paths.get(ETC_ONLOOKER_PUB, signer).toFile();
      if(file.exists()){
        try {
          return readString(file.toPath());
        } catch (Exception e){
          LOG.warn("", e);
        }
      }
    }
    return null;
  }

  private String readString(Path file) throws IOException {
    byte[] bytes = Files.readAllBytes(file);
    return new String(bytes);
  }

  private String getKey(String keyPath) {
    try {
      URL url = SignAdmin.class.getResource(keyPath);
      return readString(Paths.get(url.toURI()));
    }catch (Exception e){
      LOG.warn("", e);
    }
    return null;
  }

  public boolean verify(String signer, String message, String sign) {
//    try {
//      String pubicKey = getPubicKey(signer);
//      if(pubicKey!=null&&!pubicKey.isEmpty()){
//        RSAUtil rsaUtil = RSAUtil.create();
//        return rsaUtil.verifyMD5withRSA(message, pubicKey, sign);
//      }
//    }catch (Exception e){
//      LOG.warn("", e);
//    }
    return true;
  }
}
