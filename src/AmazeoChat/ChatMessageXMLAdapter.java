package AmazeoChat;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.IOException;
import java.io.StringReader;
import java.util.Scanner;

/**
 * Created by hugiasgeirsson on 12/02/15.
 */
public class ChatMessageXMLAdapter {

    Document documentRepresentation;
    Element messageRootElement;
    Color messageColor;
    String encryptedColor;
    ChatSession session;
    byte[] aesEncryptionKey;
    int caesarKey;

    public ChatMessageXMLAdapter(ChatSession session){
        this.session = session;
    }

    // Convert AmazeoChat.ChatMessage to XML

    public String chatMessageToXML(ChatMessage chatMessage){
        String message = chatMessage.getMessageString();
        String author = chatMessage.getMessageAuthor();
        String fileName = chatMessage.getFileName();
        long fileSize = chatMessage.getFileSize();
        Color color = chatMessage.getMessageColor();
        String requestAnswer = chatMessage.getRequestAnswer();
        int fileRequestPort = chatMessage.getFileRequestPort();
        String keyRequestType = chatMessage.getKeyRequestType();

        message = message.replaceAll("&","&amp;");
        message = message.replaceAll("<","&lt;");
        message = message.replaceAll(">","&gt;");
        message = message.replaceAll("\"","&quot;");
        message = message.replaceAll("\'","&apos;");

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
        } else if (chatMessage.getMessageType().equals("keyrequest")) {
            return "<keyrequest type=\"" + keyRequestType + "\">" +
                    message +
                    "</keyrequest>";
        } else if (chatMessage.getMessageType().equals("keyresponse")) {
            return "<keyresponse key=\"" + session.encryptDecrypt.getKeyString(session.getSessionEncryption()) + "\">" +
                    message +
                    "</keyresponse>";
        }
        else {

            String content = "<text color=\"" + rgbToHex(color) + "\">" + message + "</text>";

            if(!session.getSessionEncryption().equals("None")){
                content = "<encrypted type=\"" + session.getSessionEncryption() + "\">"
                        + session.encryptDecrypt.encryptWithType(content, session.getSessionEncryption())
                        + "</encrypted>";
            }

            return "<message sender=\"" + author + "\">" +
                    content + "</message>";
        }
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

    // Convert XML to AmazeoChat.ChatMessage

    public void getDocumentFromXML(String XML){
        DocumentBuilderFactory documentBuilder = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder db = documentBuilder.newDocumentBuilder();
            InputSource input = new InputSource(new StringReader(XML));
            documentRepresentation = db.parse(input);
            messageRootElement = documentRepresentation.getDocumentElement();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getMessageType(){
        String messageType;
        messageType = messageRootElement.getTagName();
        if (messageRootElement.getFirstChild() != null){
            if(messageRootElement.getFirstChild().getNodeName().equals("disconnect")){
                messageType = "disconnect";}
        }
        return messageType;
    }

    public String getMessageSender(){
        return messageRootElement.getAttribute("sender");
    }

    public String getRequestReply(){
        return messageRootElement.getAttribute("reply");
    }

    public String getKeyRequestType() {return messageRootElement.getAttribute("type");}

    public String getFileRequestFileName(){
        return messageRootElement.getAttribute("name");
    }

    public String getFileRequestFileSize(){
        return messageRootElement.getAttribute("size");
    }

    public int getFileResponsePort(){
        return Integer.parseInt(messageRootElement.getAttribute("port"));
    }

    public String getMessageContents(){
        String messageContents = "";
        String text;
        String color = "#ff0000";
        Node contentPart = messageRootElement.getFirstChild();
        while (contentPart != null) {
            if (contentPart.getNodeName().equals("text")) {
                messageContents = messageContents + contentPart.getTextContent();
                color = contentPart.getAttributes().getNamedItem("color").getNodeValue();
            } else if (contentPart.getNodeName().equals("encrypted")) {
                String encryptionType = contentPart.getAttributes().getNamedItem("type").getNodeValue();
                String encryptedPart = contentPart.getTextContent();
                String decryptedPart = decryptString(encryptedPart, encryptionType);
                if(decryptedPart.startsWith("<text")){
                    text = textScanner(decryptedPart);
                    color = encryptedColor;
                } else {
                    text = decryptedPart;
                }
                messageContents = messageContents + text;
            }
            contentPart = contentPart.getNextSibling();
        }
        messageColor = Color.decode(color);
        return messageContents;
    }

    public String getRequestMessage(){
        return  messageRootElement.getTextContent();
    }

    public String decryptString(String encryptedString, String encryptionType){
        if(encryptionType.equals("caesar")){
            if(caesarKey != 0){
                return session.encryptDecrypt.decryptCaesar(encryptedString, caesarKey);
            }
        }
        if(encryptionType.equals("AES")) {
            if (aesEncryptionKey != null) {
                return session.encryptDecrypt.decryptStringAES(encryptedString, aesEncryptionKey);
            }
        }
        return "__ENCRYPTED STRING, NEED KEY FROM USER___ ";
    }

    public String textScanner(String textXML){
        // Get text string

        textXML = textXML.replaceAll("&amp;","&");
        textXML = textXML.replaceAll("&lt;","<");
        textXML = textXML.replaceAll("&gt;",">");
        textXML = textXML.replaceAll("&quot;","\"");
        textXML = textXML.replaceAll("&apos;","\'");
        textXML = textXML.replaceAll("&;"," ");

        String message = "";
        Scanner textParser = new Scanner(textXML);
        textParser.useDelimiter("<|>");
        if (textParser.hasNext()) {
            String text = "";
            while (!text.startsWith("text") & textParser.hasNext()) {
                text = textParser.next();
            }
            textParser.useDelimiter("\\Z");
            text = textParser.next();
            if (text.startsWith(">")) {
                text = text.substring(1);
            }
            if (text.endsWith("</text>")) {
                text = text.substring(0, text.length() - 7);
            }
            message = text;
        }

        // Get color
        Scanner colorParser = new Scanner(textXML);
        colorParser.useDelimiter("<text\\s|</text>");
        String hexColor;
        String text = "";
        while (!text.startsWith("color=") & colorParser.hasNext()) {
            text = colorParser.next();
        }
        String[] splitMessage = text.split("color=\"");
        hexColor = splitMessage[1].split("\"")[0];
        if (hexColor.length() == 7 & hexColor.startsWith("#")) {
            encryptedColor = hexColor;
        }

        return message;
    }

    public ChatMessage xmlToChatMessage(){
        String messageType = getMessageType();
        if (messageType.equals("message")){
            String sender = getMessageSender();
            String contents = getMessageContents();
            return new ChatMessage(sender, messageColor, contents, messageType);
        }         else if(messageType.equals("disconnect")){
            String sender = getMessageSender();
            return new ChatMessage(sender, Color.red, "has disconnected...", "disconnect");
        }
        else if (messageType.equals("request")){
            String reply = getRequestReply();
            String contents = getRequestMessage();
            return new ChatMessage(contents, reply);
        }
        else if (messageType.equals("filerequest")){
            String fileName = getFileRequestFileName();
            int fileSize = Integer.parseInt(getFileRequestFileSize());
            String contents = getRequestMessage();
            return new ChatMessage(fileName, contents, fileSize);
        }
        else if (messageType.equals("fileresponse")){
            String reply = getRequestReply();
            int port = getFileResponsePort();
            String contents = getRequestMessage();
            ChatMessage response = new ChatMessage(contents, reply);
            response.setFileRequestPort(port);
            response.setMessageType("fileresponse");
            return response;
        }
        else if (messageType.equals("keyrequest")){
            String contents = getRequestMessage();
            ChatMessage keyRequest = new ChatMessage(contents);
            keyRequest.setMessageType("keyrequest");
            keyRequest.setKeyRequestType(getKeyRequestType());
            return keyRequest;
        }
        else if (messageType.equals("keyresponse")){
            String contents = getRequestMessage();
            ChatMessage keyResponse = new ChatMessage(contents);
            keyResponse.setMessageType("keyresponse");
            return keyResponse;
        }
        else {
            return new ChatMessage("Unknown", Color.black, "Unknown message type received", "message");
        }
    }

    public ChatMessage xmlToChatMessage(String XML, ChatConnection parentConnection){
        getDocumentFromXML(XML);
        aesEncryptionKey = parentConnection.getConnectedUserAESKey();
        caesarKey = parentConnection.getConnectedUserCaesarKey();
        ChatMessage message = xmlToChatMessage();
        aesEncryptionKey = "".getBytes();
        caesarKey = 0;
        return message;
    }

}
