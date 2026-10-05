package pe.edu.utp.sigpi.service;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.*;
import java.util.Base64;
public final class Passwords {
 private Passwords() {}
 public static String hash(String password) {
  if(password==null || password.length()<8 || password.length()>128) throw new IllegalArgumentException("La contraseña debe tener entre 8 y 128 caracteres");
  byte[] salt=new byte[16]; new SecureRandom().nextBytes(salt);
  return "pbkdf2$210000$"+Base64.getEncoder().encodeToString(salt)+"$"+derive(password,salt,210000);
 }
 private static String derive(String password,byte[] salt,int rounds) {
  PBEKeySpec spec=new PBEKeySpec(password.toCharArray(),salt,rounds,256);
  try {return Base64.getEncoder().encodeToString(SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded());}
  catch(Exception e){throw new IllegalStateException(e);} finally {spec.clearPassword();}
 }
 public static boolean matches(String password,String encoded) {
  if(password==null || password.length()>128 || encoded==null) return false;
  try {String[] p=encoded.split("\\$"); return p.length==4 && p[0].equals("pbkdf2") && MessageDigest.isEqual(derive(password,Base64.getDecoder().decode(p[2]),Integer.parseInt(p[1])).getBytes(java.nio.charset.StandardCharsets.UTF_8),p[3].getBytes(java.nio.charset.StandardCharsets.UTF_8));} catch(Exception e){return false;}
 }
}
