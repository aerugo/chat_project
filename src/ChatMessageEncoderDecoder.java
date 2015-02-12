import java.awt.*;
import java.util.Scanner;

/**
 * Created by hugiasgeirsson on 12/02/15.
 */
public class ChatMessageEncoderDecoder {

    public ChatMessageEncoderDecoder(){}

    public String chatMessageToXML(ChatMessage chatMessage){
        String message = chatMessage.getMessageString();
        String author = chatMessage.getMessageAuthor();
        Color color = chatMessage.getMessageColor();
        return "<message sender=\"" + author + "\">" +
                "<text color=" + rgbToHex(color) + "\">" +
                message +
                "</text></message>";
    }

    public ChatMessage xmlToChatMessage(String XML){

        String author = "Unknown";
        Color color = new Color(0,0,0);
        String message = "Error: No message retrieved.";
        String messageType = "message";

        // Get author name
        Scanner authorParser = new Scanner(XML);
        authorParser.useDelimiter("<message\\s|</message>");

        if (authorParser.hasNext()){
            String next = authorParser.next();
            String[] splitMessage = next.split("sender=\"");
            if(splitMessage.length > 1){
                author = splitMessage[1].split("\"")[0];
            } else{
                author = splitMessage[0].split("\"")[0];
            }
        }

        // Get color
        Scanner colorParser = new Scanner(XML);
        colorParser.useDelimiter("<text\\s|</text>");
        String hexColor;
        String next = "";
        while (!next.startsWith("color=") & colorParser.hasNext()){
            next = colorParser.next();
        }
        String[] splitMessage = next.split("color=\"");
        hexColor = splitMessage[0].split("\"")[0].substring(6);
        System.out.println(hexColor);
        if(hexColor.length()==7 & hexColor.startsWith("#")){
            color = Color.decode(hexColor);
        }

        // Get message
        Scanner messageParser = new Scanner(XML);
        messageParser.useDelimiter("<|>");
        if(messageParser.hasNext()) {
            next = "";
            while(!next.startsWith("message")){
                next = messageParser.next();
            }
            while (!next.startsWith("text")){
                next = messageParser.next();
            }
            messageParser.useDelimiter("\\Z");
            next = messageParser.next();
            if(next.startsWith(">")){
                next = next.substring(1);
            }
            if(next.endsWith("</message>")){
                next = next.substring(0, next.length()-10);
            }
            if(next.endsWith("</text>")){
                next = next.substring(0, next.length()-7);
            }
            message = next;
        }

        return new ChatMessage(author, color, message, messageType);
    }

    public String rgbToHex(Color color){
        String red = Integer.toHexString(color.getRed());
        String green = Integer.toHexString(color.getGreen());
        String blue = Integer.toHexString(color.getBlue());
        String hex = "#";
        String[] colors = {red, green, blue};
        for(String c : colors){
            if(c.length()<2){
                hex = hex + "0" + c;
            }else{
                hex = hex + c;
            }
        }
        return hex.toUpperCase();
    }
}
