/*
 * Created by IntelliJ IDEA.
 * User: rademach
 * Date: 20.03.2002
 * Time: 16:27:55
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package quasarusers.util;

import java.io.ObjectOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Diese Klasse verschlüsselt einen String nach MD5. Eine Entschlüsslung
 * ist nicht möglich.
 *
 * @author Matthias Rademacher
 */
public class MD5Encoder {
  public MD5Encoder() {
  };

  public String encode(String string) {
    try {
      MessageDigest digest = MessageDigest.getInstance("MD5");
      ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
      ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);

      objectStream.writeObject(string);
      digest.update(byteStream.toByteArray());

      byte[] erg = digest.digest();

      StringBuffer hexString = new StringBuffer();
      for (int i = 0; i < erg.length; i++)
        hexString.append(Integer.toHexString(0xFF & erg[i]));

      return hexString.toString();
    }
    catch (NoSuchAlgorithmException e) {
      e.printStackTrace(); // nicht möglich
    }
    catch (IOException e) {
      e.printStackTrace(); // nicht möglich
    }

    return "";
  }
}
