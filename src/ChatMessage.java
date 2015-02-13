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

    public ChatMessage(String author, Color color, String message, String type){
        this.messageAuthor = author;
        this.messageColor = color;
        this.messageString = message;
        this.messageType = type;
    }

    public ChatMessage(String message, String requestAnswer){
        this.messageType = "request";
        this.messageAuthor = "Unknown";
        this.messageString = message;
        this.messageColor = new Color(255,255,255);
        this.requestAnswer = requestAnswer;
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
}
