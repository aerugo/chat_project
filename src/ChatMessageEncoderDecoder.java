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
        String fileName = chatMessage.getFileName();
        long fileSize = chatMessage.getFileSize();
        Color color = chatMessage.getMessageColor();
        String requestAnswer = chatMessage.getRequestAnswer();
        int fileRequestPort = chatMessage.getFileRequestPort();

        if(chatMessage.getMessageType().equals("disconnect")){
            return "<message sender=\"" + author + "\">" +
                    "<disconnect />" +
                    "</message>";
        } else if(chatMessage.getMessageType().equals("request")) {
            return "<request reply=\"" + requestAnswer + "\">" +
                    message +
                    "</request>";
        } else if (chatMessage.getMessageType().equals("filerequest")) {
            return "<filerequest name=\"" + fileName + "\" size=\"" + fileSize + "\">" +
                    message +
                    "</filerequest>";
        } else if (chatMessage.getMessageType().equals("fileresponse")) {
            return "<fileresponse reply=\"" + requestAnswer + "\" port=\"" + fileRequestPort + "\">" +
                    message +
                    "</fileresponse>";
        }
        else {
            return "<message sender=\"" + author + "\">" +
                    "<text color=" + rgbToHex(color) + "\">" +
                    message +
                    "</text></message>";
        }
    }

    public ChatMessage xmlToChatMessage(String XML){

        String author = "Unknown";
        Color color = new Color(0,0,0);
        String message = "Error: No message retrieved.";
        String messageType = "message";
        String requestAnswer = "";
        String fileName = "No file";
        int fileRequestPort = 0;
        int fileSize = 0;

        // Check if request and get request message
        if(XML.startsWith("<request")) {
            Scanner requestParser = new Scanner(XML);
            requestParser.useDelimiter("<request\\s|</request>");

            String next = "";
            if (requestParser.hasNext()) {
                next = requestParser.next();
                String[] splitMessage = next.split("reply=\"");
                if (splitMessage.length > 1) {
                    requestAnswer = splitMessage[1].split("\"")[0];
                }
            }

            Scanner requestMessageParser = new Scanner(XML);
            requestMessageParser.useDelimiter("<|>");
            if(requestMessageParser.hasNext()) {
                next = "";
                while(!next.startsWith("request")){
                    next = requestMessageParser.next();
                }
                requestMessageParser.useDelimiter("\\Z");
                next = requestMessageParser.next();
                if(next.startsWith(">")){
                    next = next.substring(1);
                }
                if(next.endsWith("</request>")){
                    next = next.substring(0, next.length()-10);
                }
                message = next;
            }
            return new ChatMessage(message, requestAnswer);
        }

        //Check if message and get message
        if(XML.startsWith("<message")) {
            // Get author name
            Scanner authorParser = new Scanner(XML);
            authorParser.useDelimiter("<message\\s|</message>");

            String next = "";
            if (authorParser.hasNext()) {
                next = authorParser.next();
                String[] splitMessage = next.split("sender=\"");
                if (splitMessage.length > 1) {
                    author = splitMessage[1].split("\"")[0];
                } else {
                    author = splitMessage[0].split("\"")[0];
                }
            }

            // Get message type
            if (next.endsWith("<disconnect />")) {
                return new ChatMessage(author, color, "has disconnected...", "disconnect");
            }

            // Get color
            Scanner colorParser = new Scanner(XML);
            colorParser.useDelimiter("<text\\s|</text>");
            String hexColor;
            next = "";
            while (!next.startsWith("color=") & colorParser.hasNext()) {
                next = colorParser.next();
            }
            String[] splitMessage = next.split("color=\"");
            hexColor = splitMessage[0].split("\"")[0].substring(6);
            if (hexColor.length() == 7 & hexColor.startsWith("#")) {
                color = Color.decode(hexColor);
            }

            // Get message
            Scanner messageParser = new Scanner(XML);
            messageParser.useDelimiter("<|>");
            if (messageParser.hasNext()) {
                next = "";
                while (!next.startsWith("message")) {
                    next = messageParser.next();
                }
                while (!next.startsWith("text")) {
                    next = messageParser.next();
                }
                messageParser.useDelimiter("\\Z");
                next = messageParser.next();
                if (next.startsWith(">")) {
                    next = next.substring(1);
                }
                if (next.endsWith("</message>")) {
                    next = next.substring(0, next.length() - 10);
                }
                if (next.endsWith("</text>")) {
                    next = next.substring(0, next.length() - 7);
                }
                message = next;
            }

            // Look for encrypted parts
            Scanner encryptionParser = new Scanner(message);
            messageParser.useDelimiter("<|>");
            String nextPart = "";
            String fullMessage = "";
            while (messageParser.hasNext()) {
                while (!next.startsWith("encrypted")) {
                    next = messageParser.next();
                }
            }


            return new ChatMessage(author, color, message, messageType);
        }

        // Check if filerequest and get request message
        if(XML.startsWith("<filerequest")) {
            Scanner requestParser = new Scanner(XML);
            requestParser.useDelimiter("<filerequest\\s|</filerequest>");

            String next = "";
            if (requestParser.hasNext()) {
                next = requestParser.next();
                String[] nameSplit = next.split("name=\"");
                if (nameSplit.length > 1) {
                    fileName = nameSplit[1].split("\"")[0];}
                String[] sizeSplit = next.split("size=\"");
                if (sizeSplit.length > 1) {
                    fileSize = Integer.parseInt(sizeSplit[1].split("\"")[0]);}
            }

            Scanner requestMessageParser = new Scanner(XML);
            requestMessageParser.useDelimiter("<|>");
            if(requestMessageParser.hasNext()) {
                next = "";
                while(!next.startsWith("filerequest")){
                    next = requestMessageParser.next();
                }
                requestMessageParser.useDelimiter("\\Z");
                next = requestMessageParser.next();
                if(next.startsWith(">")){
                    next = next.substring(1);
                }
                if(next.endsWith("</filerequest>")){
                    next = next.substring(0, next.length()-14);
                }
                message = next;

            }
            return new ChatMessage(fileName, message, fileSize);
        }

        // Check if fileresponse and get fileresponse message
        if(XML.startsWith("<fileresponse")) {
            Scanner requestParser = new Scanner(XML);
            requestParser.useDelimiter("<fileresponse\\s|</fileresponse>");

            String next = "";
            if (requestParser.hasNext()) {
                next = requestParser.next();
                String[] nameSplit = next.split("reply=\"");
                if (nameSplit.length > 1) {
                    requestAnswer = nameSplit[1].split("\"")[0];}
                String[] sizeSplit = next.split("port=\"");
                if (sizeSplit.length > 1) {
                    fileRequestPort = Integer.parseInt(sizeSplit[1].split("\"")[0]);}
            }

            Scanner requestMessageParser = new Scanner(XML);
            requestMessageParser.useDelimiter("<|>");
            if(requestMessageParser.hasNext()) {
                next = "";
                while(!next.startsWith("fileresponse")){
                    next = requestMessageParser.next();
                }
                requestMessageParser.useDelimiter("\\Z");
                next = requestMessageParser.next();
                if(next.startsWith(">")){
                    next = next.substring(1);
                }
                if(next.endsWith("</fileresponse>")){
                    next = next.substring(0, next.length()-15);
                }
                message = next;

            }
            ChatMessage returnMessage = new ChatMessage(message, requestAnswer);
            returnMessage.setFileRequestPort(fileRequestPort);
            returnMessage.setMessageType("fileresponse");
            return returnMessage;
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
