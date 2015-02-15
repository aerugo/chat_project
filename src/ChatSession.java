import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by hugiasgeirsson on 08/02/15.
 */
public class ChatSession {
    private String userName;
    private Color messageColor;
    private String chatName;
    private String hostAddress;
    private String connectRequestMessage = "No request message";
    private int port;
    private ServerSocket serverSocket;
    private ChatSessionWindow window;
    private ArrayList<ChatConnection> connectionList;
    private DefaultComboBoxModel userChooserModel;
    ChatMessageEncoderDecoder encoderDecoder;
    Boolean connected;

    public ChatSession(String hostAddress, int port, String userName, String chatName) {
        this.hostAddress = hostAddress;
        this.port = port;
        this.chatName = chatName;
        this.encoderDecoder = new ChatMessageEncoderDecoder();
        userChooserModel = new DefaultComboBoxModel();
        setUserName(userName);
        setMessageColor(new Color(0,0,255));
        connectionList = new ArrayList<ChatConnection>();
        startListening();
    }

    public ChatSession(ServerSocket serverSocket, String userName, String chatName){
        this.hostAddress = "server";
        this.port = serverSocket.getLocalPort();
        this.chatName = chatName;
        this.encoderDecoder = new ChatMessageEncoderDecoder();
        this.connected = true;
        userChooserModel = new DefaultComboBoxModel();
        setUserName(userName);
        setMessageColor(new Color(0,0,255));
        connectionList = new ArrayList<ChatConnection>();
    }

    public ChatMessage inputToChatMessage(String author, Color color, String message){
        return new ChatMessage(author, color, message, "message");
    }

    public void startListening(){
        if(getHostAddress().equals("server")){
            try {
                serverSocket = new ServerSocket(port);
            } catch (IOException e){
                connected = false;
                System.out.println("Could not listen on port: " + port);
                new ChatErrorPromptWindow("Could not listen on port: " + port);
            }

            ChatServerDaemon serverDaemon = new ChatServerDaemon(serverSocket, this);
            serverDaemon.start();
            new ChatServerDaemonWindow(serverDaemon);
            connected = true;

        } else{
            ChatConnection clientConnection = new ChatConnection(this.getServerSocket(), this);
            clientConnection.start();
            addConnection(clientConnection);
        }
    }

    public void sendMessageToAll(ChatMessage message){
        if(!connectionList.isEmpty()){
            for(ChatConnection connection : connectionList){
                connection.sendMessage(message);
            }
        }
    }

    public void disconnectFromSession(){
        ChatMessage disconnectMessage = new ChatMessage(userName,Color.red,"Disconnected...","disconnect");
        sendMessageToAll(new ChatMessage(userName, new Color(255,0,0), "disconnected", "message"));
        if(hostAddress.equals("server")){
            sendMessageToAll(new ChatMessage(userName, new Color(255,0,0), "SERVER DISCONNECTED. Chat is no more!", "message"));
        }
        sendMessageToAll(disconnectMessage);
        if(hostAddress.equals("server")){
            for(ChatConnection connection : connectionList){
                connection.killConnection();
            }
        }
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Color getMessageColor() {
        return messageColor;
    }

    public void setMessageColor(Color messageColor) {
        this.messageColor = messageColor;
    }

    public String getChatName() {
        return chatName;
    }

    public String getHostAddress() {
        return hostAddress;
    }

    public Socket getServerSocket(){
        try{
            Socket socket = new Socket(hostAddress, port);
            connected = true;
            return socket;
        }catch(IOException e) {
            System.out.println("getServerSocket failed: " + e);
            connected = false;
            return null;}
    }

    public ChatSessionWindow getWindow() {
        return window;
    }

    public void setWindow(ChatSessionWindow window) {
        this.window = window;
    }


    public void addConnection(ChatConnection connection) {
        this.connectionList.add(connection);
        userChooserModel.addElement(connection);
    }

    public ArrayList<ChatConnection> getConnectionList() {
        return connectionList;
    }

    public ComboBoxModel getUserChooserModel() {
        return userChooserModel;
    }

    public void removeConnectionFromUserChooser(ChatConnection connection){
        userChooserModel.removeElement(connection);
    }

    public void setConnectRequestMessage(String connectRequestMessage) {
        this.connectRequestMessage = connectRequestMessage;
    }

    public String getConnectRequestMessage() {
        return connectRequestMessage;
    }
}
