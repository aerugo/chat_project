import com.sun.crypto.provider.AESCipher;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by hugiasgeirsson on 20/02/15.
 */
public class ChatMessageEncryptDecrypt {
    private byte[] keyContent;
    private SecretKeySpec aesKey;
    private byte[] cipherData;
    private String caesarKey;

    public ChatMessageEncryptDecrypt(){}

    public void generateAESKey(){
        try {
            KeyGenerator aesGenerator = KeyGenerator.getInstance("AES");
            aesGenerator.init(128);
            aesKey = (SecretKeySpec)aesGenerator.generateKey();
            keyContent = aesKey.getEncoded();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public String encryptStringAES(String string) throws InvalidKeyException {
        try {
            byte[] dataToEncrypt = string.getBytes();
            Cipher aesCipher = Cipher.getInstance("AES");
            aesCipher.init(Cipher.ENCRYPT_MODE, aesKey);
            cipherData = aesCipher.doFinal(dataToEncrypt);
            return DatatypeConverter.printHexBinary(cipherData);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return "__ENCRYPTION ERROR___\"";
    }

    public String decryptStringAES(String string) throws InvalidKeyException {
        try {
            byte[] dataToDecrypt = DatatypeConverter.parseHexBinary(string);
            SecretKeySpec decodeKey = new SecretKeySpec(keyContent, "AES");
            Cipher aesCipher = Cipher.getInstance("AES");
            aesCipher.init(Cipher.DECRYPT_MODE, decodeKey);
            byte[] decryptedData = aesCipher.doFinal(dataToDecrypt);
            return new String(decryptedData, "UTF-8");
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "___DECRYPTION ERROR___";
    }

    public static void main(String[] args){

        String encrypted = "";
        String decrypted = "";
        ChatMessageEncryptDecrypt encryptDecrypt = new ChatMessageEncryptDecrypt();

        encryptDecrypt.generateAESKey();

        String string = "Jag </vill> \"kunna krypteras!";

        try {
            encrypted = encryptDecrypt.encryptStringAES(string);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        System.out.println(encrypted);

        try {
            decrypted = encryptDecrypt.decryptStringAES(encrypted);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        System.out.println(decrypted);

    }

}
