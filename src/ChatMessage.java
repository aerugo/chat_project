import java.awt.*;
import java.io.Serializable;

/**
 * Created by hugiasgeirsson on 08/02/15.
 */
public class ChatMessage implements Serializable{
    private String messageXML;
    private String messageAuthor;
    private Color messageColor;
    private String messageType;

    public ChatMessage(String author, Color color, String message, String type){
        this.messageAuthor = author;
        this.messageColor = color;
        this.messageXML = message;
        this.messageType = type;
    }

    public String getMessageXML() {
        return messageXML;
    }

    public String getMessageAuthor() {
        return messageAuthor;
    }

    public Color getMessageColor() {
        return messageColor;
    }

    public String getMessageType() {return messageType; }
}
