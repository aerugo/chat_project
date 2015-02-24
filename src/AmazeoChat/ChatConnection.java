package AmazeoChat;

import java.awt.*;
import java.io.*;
import java.net.Socket;

/**
 * Created by hugiasgeirsson on 10/02/15.
 */
public class ChatConnection extends Thread{
    private Socket clientSocket;
    private ChatSession session;
    private String connectedUserName;
    private byte[] connectedUserAESKey;
    private int connectedUserCaesarKey;
    private String requestMessage;
    private File fileToTransfer;
    private ChatFileTransfer fileSend;
    private PrintWriter out;
    private BufferedReader in;
    private boolean done;
    ChatKeyRequestWindow keyRequestWindow;


    public ChatConnection(Socket clientSocket, ChatSession session){
        this.clientSocket = clientSocket;
        this.session = session;
        this.done = false;
        if(!session.getHostAddress().equals("server")){
            this.connectedUserName = "Server host";
            System.out.println("Server running...");
        }
    }

    public void sendFileRequest(String fileName, String requestMessage, int fileSize){
        sendChatMessageAsXML(new ChatMessage(fileName, requestMessage, fileSize));
    }

    public void sendFileResponse(String message, String response){
        ChatMessage fr = new ChatMessage(message, response);
        fr.setFileRequestPort(9822);
        fr.setMessageType("fileresponse");
        sendChatMessageAsXML(fr);
    }

    public void sendKeyRequest(String message, String type){
        ChatMessage keyRequest = new ChatMessage(message);
        keyRequest.setMessageType("keyrequest");
        keyRequest.setKeyRequestType(type);
        sendChatMessageAsXML(keyRequest);

    }

    public void sendKeyResponse(String key){
        ChatMessage keyResponse = new ChatMessage(key);
        keyResponse.setMessageType("keyresponse");
        sendChatMessageAsXML(keyResponse);
    }

    public void sendChatResponse(String message, String response){
        sendChatMessageAsXML(new ChatMessage("Ok!", "yes"));
    }

    public void sendChatMessageAsXML(ChatMessage message){
        String xmlMessage = session.xmlAdapter.chatMessageToXML(message);
        System.out.println(xmlMessage);
        try{
            out.println(xmlMessage);
            System.out.println("Sent message");
            if(message.getMessageType().equals("disconnect")){
                done = true;
            }
        }catch(Exception e){
            System.out.println("read failed: " + e);
            session.getWindow().printNotification("read failed: " + e);
        }
    }

    public void run(){

        try{
            out = new PrintWriter(clientSocket.getOutputStream(), true);
        }catch(Exception e){
            System.out.println("getOutputStream failed: " + e);
            return;
        }

        try{
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        }catch(Exception e){
            System.out.println("getInputStream failed: " + e);
            session.getWindow().printNotification("Connection not established");
            return;
        }

        // Connection successful
        System.out.println("Connection Established: "
                + clientSocket.getInetAddress());

        //Client request pending
        if(!session.getHostAddress().equals("server")) {
            System.out.println("Client running...");
            sendChatMessageAsXML(new ChatMessage(session.getUserName() + ": " + session.getConnectRequestMessage(), "request"));
            boolean pending = true;
            while (pending) {

                ChatMessage message = getMessageFromBuffer();

                if (message.getRequestAnswer().equals("yes")) {
                    sendChatMessageAsXML(new ChatMessage(session.getUserName(), new Color(0, 255, 0), "has connected!", "message"));
                } else {
                    done = true;
                }

                pending = false;
            }
        }

        //Listening for messages and username updates
        while(!done){

            final ChatMessage message = getMessageFromBuffer();

            System.out.println("Message type: "+ message.getMessageType());

            String echo = "Recieved: ("
                    + clientSocket.getInetAddress()
                    + ") ";

            if(!done){
                if(message.getMessageType().equals("request")||
                        message.getMessageType().equals("filerequest")||
                        message.getMessageType().equals("fileresponse")) {
                    this.requestMessage = message.getMessageString();
                }

                if(message.getMessageType().equals("filerequest")) {
                    System.out.println("Filerequest received!");
                    new ChatFileTransfer(message.getFileName(),message.getMessageString(),(int)message.getFileSize(), this);
                    String requestMessage = message.getMessageString();
                    String fileName = message.getFileName();
                    long fileSize = message.getFileSize();
                }

                else if(message.getMessageType().equals("fileresponse")) {
                    System.out.println("Fileresponse received!");
                    if (message.getRequestAnswer().equals("yes")){
                        Runnable sendTask = new Runnable() {
                            public void run() {
                                fileSend.getSendWindow().setRequestReply("File accepted! Message: " + message.getMessageString());
                                fileSend.openSendFileConnection(message.getFileRequestPort());
                                fileSend.sendFile(fileToTransfer);
                            }
                        };
                        new Thread(sendTask).start();
                    } else{
                        fileSend.getSendWindow().setRequestReply("Transfer not accepted. Message: "+ message.getMessageString());
                    }
                }
                else if(message.getMessageType().equals("keyrequest")) {
                    System.out.println("Keyrequest received!");
                    new ChatKeyResponseWindow(this, message);
                }
                else if(message.getMessageType().equals("keyresponse")) {
                    System.out.println("Keyresponse received!");
                    String key = message.getMessageString();
                    setConnectedUserKey(keyRequestWindow.getRequestType(), key);
                    System.out.println("Received key: " + key);
                }
                if(message.getMessageType().equals("message")) {
                    session.getWindow().printMessage(message);

                    if (session.getHostAddress().equals("server") & !done) {
                        session.forwardMessageToAll(message, this);
                    }
                }

                System.out.println(echo);

                if(session.getHostAddress().equals("server")) {
                    try {
                        connectedUserName = message.getMessageAuthor();
                    } catch (NullPointerException e) {
                        System.out.println("No name available");
                        connectedUserName = "Unknown";
                    }
                }
            }

        }

        // Reach here when connection is done

        disconnect();
    }

    private void disconnect(){
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.flush();
            out.close();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try{
            session.getConnectionList().remove(this);
            session.removeConnectionFromUserChooser(this);
            System.out.println(this + " disconnected!");
        }catch(Exception e){
            System.out.println("Error when disconnecting: " + e);
        }
    }

    private ChatMessage getMessageFromBuffer(){
        String buffer = "";
        ChatMessage message;
        String messageType = "unknown";

        try {
            if (messageType.equals("unknown")){
                buffer = in.readLine();
                if(buffer.startsWith("<message")){
                    messageType = "message";
                }
                if(buffer.startsWith("<request")){
                    messageType = "request";
                }
                if(buffer.startsWith("<filerequest")){
                    messageType = "filerequest";
                }
                if(buffer.startsWith("<fileresponse")){
                    messageType = "fileresponse";
                }
                if(buffer.startsWith("<keyrequest")){
                    messageType = "keyrequest";
                }
                if(buffer.startsWith("<keyresponse")){
                    messageType = "keyresponse";
                }
            }
            while (!buffer.endsWith("</"+messageType+">")) {
                buffer = buffer + in.readLine();
            }
            message = session.xmlAdapter.xmlToChatMessage(buffer, this);
        }catch(IOException e){
            System.out.println( this + " read failed: " + e);
            message = new ChatMessage("System",new Color(255,0,0),"Could not get message...","error");
        }
        return message;
    }

    public void killConnection(){
        done = true;
    }

    public String getConnectedUserName() {
        return connectedUserName;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public void setSession(ChatSession session) {
        this.session = session;
    }

    public void setFileToTransfer(File fileToTransfer) {
        this.fileToTransfer = fileToTransfer;
    }

    public void setFileSend(ChatFileTransfer fileSend) {
        this.fileSend = fileSend;
    }

    public String getRequestMessage() {
        return requestMessage;
    }

    public void setConnectedUserKey(String type, String key) {
        if(type.equals("AES")){
            connectedUserAESKey = session.encryptDecrypt.keyStringToBytes(key);
            keyRequestWindow.requestStatus.setText("Key received!");
        }
        if(type.equals("caesar")){
            connectedUserCaesarKey = Integer.parseInt(key);
            keyRequestWindow.requestStatus.setText("Key received!");
        }
    }

    public boolean hasConnectedUserKey(String type){
        if(type.equals("caesar")){
            if(connectedUserCaesarKey != 0){
                return true;
            }
        }
        else if(type.equals("AES")){
            if (connectedUserAESKey != null){
                return true;
            }
        }
        return false;
    }

    public byte[] getConnectedUserAESKey() {
        return connectedUserAESKey;
    }

    public int getConnectedUserCaesarKey() {
        return connectedUserCaesarKey;
    }

    public ChatSession getSession() {
        return session;
    }

    @Override
    public String toString() {
        return connectedUserName;
    }
}
