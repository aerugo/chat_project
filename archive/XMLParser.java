import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;

/**
 * Created by hugiasgeirsson on 19/02/15.
 * Parsing method based on http://www.java-samples.com/showtutorial.php?tutorialid=152
 */

public class XMLParser {

    Document documentRepresentation;
    Element messageRootElement;
    Color messageColor;

    public XMLParser() {}

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
                String decryptedPart = decryptString(encryptionType);
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

    public String decryptString(String encryptionType){
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

    public static void main(String[] args){

        XMLParser parser = new XMLParser();

        String[] messages = {"<request reply=\"request\">User: Request message</request>","<message sender=\"Tombi\"><text color=\"#0000FF\">Now this is some text that is not encrypted</text></message>","<message sender=\"Tombi\"><text color=\"#0000FF\">First normal text</text><encrypted type=\"krypto\"><text color=\"#0000FF\">Encrypted text string in message</text></encrypted><text color=\"#0000FF\">Second normal text</text></message>","<filerequest name=\"file.txt\" size=\"28333\">Accept my file dude!</filerequest>","<fileresponse reply=\"yes\" port=\"38884\"> Thanks for the file. </fileresponse>","<message sender=\"Tombi\"><disconnect /></message>"};

        for (String message : messages){
            parser.getDocumentFromXML(message);
            ChatMessage cm = parser.xmlToChatMessage();
            ChatMessageEncoderDecoder ed = new ChatMessageEncoderDecoder();
            System.out.println(ed.chatMessageToXML(cm));
        }
    }
}
