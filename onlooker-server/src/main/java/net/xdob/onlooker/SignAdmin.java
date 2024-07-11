package net.xdob.onlooker;

import com.google.common.base.Strings;
import com.ls.luava.security.RSAUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SignAdmin {
  public static final String ETC_ONLOOKER_PUB = "/etc/onlooker/pub/";
  static Logger LOG = LoggerFactory.getLogger(SignAdmin.class);

  private String getPubicKey(String signer){
    try {
      if("evo4x".equals(signer)){
        return getKey("/evo4x.pub");
      }else{
        if(!Strings.isNullOrEmpty(signer)) {
          File file = Paths.get(ETC_ONLOOKER_PUB, signer).toFile();
          if (file.exists()) {
            return readString(file.toPath());
          }
        }else{
          LOG.warn("signer is {}", signer);
        }
      }
    } catch (Exception e){
      LOG.warn("getPubicKey fail.", e);
    }
    return null;
  }

  private String readString(Path file) throws IOException {
    byte[] bytes = Files.readAllBytes(file);
    return new String(bytes);
  }

  private String getKey(String keyPath) {
    try {
      InputStream in = SignAdmin.class.getResourceAsStream(keyPath);
      if(in!=null) {
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
          String line;
          while ((line = reader.readLine()) != null) {
            contentBuilder.append(line).append("\n");
          }
        } finally {
          in.close();
        }
        return contentBuilder.toString();
      }
    }catch (Exception e){
      LOG.warn("getKey fail. keyPath="+keyPath, e);
    }
    return null;
  }

  public boolean verify(String signer, String message, String sign) {
    try {
      String pubicKey = getPubicKey(signer);
      if(pubicKey!=null&&!pubicKey.isEmpty()){
        RSAUtil rsaUtil = RSAUtil.create();
        return rsaUtil.verifySHA256WithRSA(pubicKey, message, sign);
      }
    }catch (Exception e){
      LOG.warn("sign verify false for {}.",signer);
    }
    return false;
  }
}
