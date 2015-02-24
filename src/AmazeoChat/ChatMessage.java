package AmazeoChat;

import java.awt.*;
import java.io.Serializable;

/**
 * Created by hugiasgeirsson on 08/02/15.
 */
public class ChatMessage implements Serializable{
    private String messageString;
    private String messageAuthor;
    private Color messageColor;
    private String messageType;
    private String requestAnswer = "no";
    private String fileName = "message";
    private String keyRequestType;
    private String encryptionType = "None";
    private String encryptedContent;
    private int fileRequestPort;
    private long fileSize = 0;

    // Constructor for messages
    public ChatMessage(String author, Color color, String message, String type){
        this.messageAuthor = author;
        this.messageColor = color;
        this.messageString = message;
        this.messageType = type;
    }

    // Constructor for requests
    public ChatMessage(String message, String requestAnswer){
        this.messageType = "request";
        this.messageAuthor = "Unknown";
        this.messageString = message;
        this.messageColor = new Color(255,255,255);
        this.requestAnswer = requestAnswer;
    }

    // Constructor for file requests
    public ChatMessage(String fileName, String requestMessage, int fileSize){
        this.messageType = "filerequest";
        this.messageAuthor = "Unknown";
        this.messageString = requestMessage;
        this.messageColor = new Color(255,255,255);
        this.fileName = fileName;
        this.fileSize = fileSize;
    }

    // Constructor for minimal message
    public ChatMessage(String message){
        this.messageString = message;
        this.messageColor = new Color(255,255,255);
    }

    public String getMessageString() {
        return messageString;
    }

    public String getMessageAuthor() {
        return messageAuthor;
    }

    public Color getMessageColor() {
        return messageColor;
    }

    public String getMessageType() {return messageType; }

    public String getRequestAnswer() {
        return requestAnswer;
    }

    public String getFileName() { return fileName; }

    public long getFileSize() { return fileSize; }

    public int getFileRequestPort() {
        return fileRequestPort;
    }

    public void setFileRequestPort(int fileRequestPort) {
        this.fileRequestPort = fileRequestPort;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getKeyRequestType() {
        return keyRequestType;
    }

    public void setKeyRequestType(String keyRequestType) {
        this.keyRequestType = keyRequestType;
    }
}
