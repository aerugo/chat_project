package AmazeoChat;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Created by hugiasgeirsson on 20/02/15.
 */
public class ChatMessageEncryptDecrypt {
    private KeyGenerator aesGenerator;
    private byte[] keyContent;
    private SecretKeySpec aesKey;
    private Character[] alphabet;
    private java.util.List alphaList;
    private int caesarKey;
    private ArrayList<String> knownTypes;
    DefaultComboBoxModel knownTypesModel;

    public ChatMessageEncryptDecrypt(){
        knownTypesModel = new DefaultComboBoxModel();
        knownTypes = new ArrayList<String>();

        Runnable keyGeneratorCreator = new Runnable() {
            @Override
            public void run() {
                ChatErrorPromptWindow loadingPrompt = new ChatErrorPromptWindow("Creating key generators...");
                loadingPrompt.disableClose();
                createAESKeyGenerator();
                generateCaesarKey();
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

    public void generateCaesarKey(){
        alphabet = new Character[]{'A', 'B', 'C', 'D', 'E', 'F','1', '2', '3', '4', '5', '6','7', '8', '9', '0'};
        alphaList = Arrays.asList(alphabet);
        caesarKey = 3;
        knownTypes.add("caesar");
        knownTypesModel.addElement("caesar");
    }

    public String encryptCaesar(String string){
        byte[] data = string.getBytes();
        String hexMessage = DatatypeConverter.printHexBinary(data);
        String encrypted = "";
        int index;
        for(char c : hexMessage.toCharArray()){
            index = alphaList.indexOf(c);
            if(index + caesarKey > alphabet.length - 1){
                index = index + caesarKey - alphabet.length;
            } else {
                index += caesarKey;
            }
            encrypted += alphabet[index];
        }
        return encrypted;
    }

    public String decryptCaesar(String string, int key){
        String decrypted = "";
        int index;
        for(char c : string.toCharArray()){
            index = alphaList.indexOf(c);
            if(index - key < 0){
                index = alphabet.length + (index-key);
            } else{
                index -= key;
            }
            decrypted += alphabet[index];
        }
        byte[] data = DatatypeConverter.parseHexBinary(decrypted);
        try {
            decrypted = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return decrypted;
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
        if(type.equals("caesar")){
            return encryptCaesar(message);
        }
        else return "";
    }

    public String getKeyString(String type){
        if(type.equals("AES")){
            return DatatypeConverter.printHexBinary(keyContent);

        }
        if (type.equals("caesar")){
            return Integer.toString(caesarKey);
        }
        else{
            return "";
        }
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
