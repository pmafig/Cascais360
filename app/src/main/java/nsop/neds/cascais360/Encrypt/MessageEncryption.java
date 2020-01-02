package nsop.neds.cascais360.Encrypt;

import android.util.Base64;

import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class MessageEncryption {

    final private int flags = Base64.NO_WRAP;

    public String Decrypt(String key, String message) {
        try {

            message = message.replace(" ", "+");

            key = key.replace("-", "");

            byte[] decodedToken =  Base64.decode(message, flags);


            byte[] initVector = GetInitializationVector(decodedToken);

            byte[] token = GetPassword(decodedToken);

            IvParameterSpec iv = new IvParameterSpec(initVector);
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] original = cipher.doFinal(token);

            return new String(original, "UTF-8");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public String Encrypt(String message, String key) {
        try {

            byte[] decodedToken =  message.getBytes("UTF-8");

            key = key.replace("-", "");

            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");

            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);

            byte[] iv = cipher.getIV();

            byte[] enc = cipher.doFinal(decodedToken);

            byte[] joined = JoinBytes(enc, iv);

            return Base64.encodeToString(joined, flags);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    private byte[] GetPassword(byte[] fullEncryptedPassword)
    {
        int passwordSize = fullEncryptedPassword.length - 16;

        byte[] password = Arrays.copyOfRange(fullEncryptedPassword, 0, passwordSize);

        return password;
    }

    private byte[] GetInitializationVector(byte[] fullEncryptedPassword)
    {
        int startOffSet = fullEncryptedPassword.length - 16;

        byte[] initializationVector = new byte[16];

        System.arraycopy(fullEncryptedPassword, startOffSet, initializationVector, 0, 16);

        return initializationVector;
    }

    private byte[] JoinBytes(byte[] message, byte[] iv)
    {
        byte[] result = new byte[message.length + iv.length];

        System.arraycopy(message, 0, result, 0, message.length);
        System.arraycopy(iv, 0, result, message.length, iv.length);

        return result;
    }
}
