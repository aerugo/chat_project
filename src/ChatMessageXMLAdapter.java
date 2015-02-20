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

/**
 * Created by hugiasgeirsson on 12/02/15.
 */
public class ChatMessageXMLAdapter {

    Document documentRepresentation;
    Element messageRootElement;
    Color messageColor;

    public ChatMessageXMLAdapter(ChatSession session){}

    // Convert ChatMessage to XML

    public String chatMessageToXML(ChatMessage chatMessage){
        String message = chatMessage.getMessageString();
        String author = chatMessage.getMessageAuthor();
        String fileName = chatMessage.getFileName();
        long fileSize = chatMessage.getFileSize();
        Color color = chatMessage.getMessageColor();
        String requestAnswer = chatMessage.getRequestAnswer();
        int fileRequestPort = chatMessage.getFileRequestPort();

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
        }
        else {
            return "<message sender=\"" + author + "\">" +
                    "<text color=\"" + rgbToHex(color) + "\">" +
                    message +
                    "</text></message>";
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

    // Convert XML to ChatMessage

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
        if(messageRootElement.getFirstChild().getNodeName().equals("disconnect")){
            messageType = "disconnect";
        } else {
            messageType = messageRootElement.getTagName();
        }
        return messageType;
    }

    public String getMessageSender(){
        return messageRootElement.getAttribute("sender");
    }

    public String getRequestReply(){
        return messageRootElement.getAttribute("reply");
    }

    public String getFileRequestFileName(){
        return messageRootElement.getAttribute("filename");
    }

    public String getFileRequestFileSize(){
        return messageRootElement.getAttribute("size");
    }

    public int getFileResponsePort(){
        return Integer.parseInt(messageRootElement.getAttribute("port"));
    }

    public String getMessageContents(){
        String messageContents = "";
        String color = "";
        Node contentPart = messageRootElement.getFirstChild();
        while (contentPart != null) {
            if (contentPart.getNodeName().equals("text")) {
                messageContents = messageContents + contentPart.getTextContent();
                color = contentPart.getAttributes().getNamedItem("color").getNodeValue();
            } else if (contentPart.getNodeName().equals("encrypted")) {
                String encryptionType = contentPart.getAttributes().getNamedItem("type").getNodeValue();
                String key = contentPart.getAttributes().getNamedItem("key").getNodeValue();
                String decryptedPart = decryptString(encryptionType, key);
                messageContents = messageContents + decryptedPart;
            }
            contentPart = contentPart.getNextSibling();
        }
        messageColor = Color.decode(color);
        return messageContents;
    }

    public String getRequestMessage(){
        return  messageRootElement.getTextContent();
    }

    public String decryptString(String encryptionType, String key){
        return " DECRYPTED ";
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
        else {
            return new ChatMessage("Unknown", Color.black, "Unknown message type received", "message");
        }
    }

    public ChatMessage xmlToChatMessage(String XML){
        getDocumentFromXML(XML);
        return xmlToChatMessage();
    }

}
