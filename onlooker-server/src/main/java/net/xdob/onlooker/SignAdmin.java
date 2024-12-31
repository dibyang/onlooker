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
import java.security.KeyPair;

public class SignAdmin {
  static Logger LOG = LoggerFactory.getLogger(SignAdmin.class);

  private String getPubicKey(String signer){
    try {
      if(!Strings.isNullOrEmpty(signer)) {
        File file = Paths.get(LookHelper.i.getPubDir(), signer+".pub").toFile();
        if (file.exists()) {
          return readString(file.toPath());
        }
      }else{
        LOG.warn("signer is {}", signer);
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

  public static void main(String[] args) {
    RSAUtil rsaUtil = RSAUtil.create();
    KeyPair keyPair = rsaUtil.generateKeyPair();
    String pub = rsaUtil.getKeyString(keyPair.getPublic());
    System.out.println("pub = " + pub);
    String pri = rsaUtil.getKeyString(keyPair.getPrivate());
    System.out.println("pri = " + pri);
  }
}
