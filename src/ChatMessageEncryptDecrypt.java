import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

/**
 * Created by hugiasgeirsson on 20/02/15.
 */
public class ChatMessageEncryptDecrypt {
    private KeyGenerator aesGenerator;
    private byte[] keyContent;
    private SecretKeySpec aesKey;
    private String caesarKey;
    private ArrayList<String> knownTypes;
    DefaultComboBoxModel knownTypesModel;

    public ChatMessageEncryptDecrypt(){
        knownTypesModel = new DefaultComboBoxModel();
        knownTypes = new ArrayList<String>();
        knownTypes.add("Caesar");
        knownTypesModel.addElement("Caesar");

        Runnable keyGeneratorCreator = new Runnable() {
            @Override
            public void run() {
                ChatErrorPromptWindow loadingPrompt = new ChatErrorPromptWindow("Creating key generators...");
                loadingPrompt.disableClose();
                createAESKeyGenerator();
                loadingPrompt.closeWindow();
            }
        };
        new Thread(keyGeneratorCreator).start();
    }

    public void createAESKeyGenerator(){
        try {
            this.aesGenerator = KeyGenerator.getInstance("AES");
            knownTypes.add("AES");
            knownTypesModel.addElement("AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public void generateAESKey(){
        aesGenerator.init(128);
        aesKey = (SecretKeySpec)aesGenerator.generateKey();
        keyContent = aesKey.getEncoded();
    }

    public String encryptStringAES(String string){
        try {
            byte[] dataToEncrypt = string.getBytes();
            Cipher aesCipher = Cipher.getInstance("AES");
            aesCipher.init(Cipher.ENCRYPT_MODE, aesKey);
            byte[] cipherData = aesCipher.doFinal(dataToEncrypt);
            return DatatypeConverter.printHexBinary(cipherData);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return "__ENCRYPTION ERROR___\"";
    }

    public String decryptStringAES(String string, byte[] key){
        try {
            byte[] dataToDecrypt = DatatypeConverter.parseHexBinary(string);
            SecretKeySpec decodeKey = new SecretKeySpec(key, "AES");
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
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return "___DECRYPTION ERROR___";
    }

    public String encryptWithType(String message, String type){
        if(type.equals("AES")){
            return encryptStringAES(message);
        }
        else return "";
    }

    public String decryptWithType(String message, String type, byte[] key){
        if(type.equals("AES")){
            return decryptStringAES(message, key);
        }
        else return "";
    }

    public String getAESKeyString(){
        return DatatypeConverter.printHexBinary(keyContent);
    }

    public byte[] keyStringToBytes(String key){
        return DatatypeConverter.parseHexBinary(key);
    }

    public ArrayList<String> getKnownTypes() {
        return knownTypes;
    }

    public ComboBoxModel getKnownTypesModel() {
        return knownTypesModel;
    }

}
