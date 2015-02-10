import java.awt.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by hugiasgeirsson on 08/02/15.
 */
public class ChatSession {
    private String userName;
    private Color messageColor;
    private String chatName;
    private Boolean serverMode = false;
    private String hostAddress;
    private int port;
    private ServerSocket serverSocket;
    private ChatWindow window;
    private ChatConnection connection;

    public ChatSession(String hostAddress, int port, String userName) {
        if(hostAddress.equals("server")){
            serverMode = true;
        }
        this.hostAddress = hostAddress;
        this.port = port;
        setUserName(userName);
        setMessageColor(Color.blue);
    }

    public ChatMessage inputToChatMessage(String author, Color color, String message){
        return new ChatMessage(author, color, message);
    }

    public void establishConnection(){
        if(getHostAdress().equals("server")){
            window.setTitle("Amazochat server");

            try {
                serverSocket = new ServerSocket(4444);
            } catch (IOException e){
                System.out.println("Could not listen on port: 4444");
                System.exit(-1);
            }

            ChatServerDaemon serverDaemon = new ChatServerDaemon(serverSocket, this);
            serverDaemon.start();

            System.out.println("Hery");

        } else{
            ChatConnection clientConnection = new ChatConnection(this.getServerSocket(), this);
            clientConnection.start();
            setConnection(clientConnection);
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

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public Boolean getServerMode() {
        return serverMode;
    }

    public String getHostAdress() {
        return hostAddress;
    }

    public Socket getServerSocket(){
        try{
            return new Socket(hostAddress, port);
        }catch(IOException e) {
            System.out.println("getOutputStream failed: " + e);
            return null;}
    }

    public ChatWindow getWindow() {
        return window;
    }

    public void setWindow(ChatWindow window) {
        this.window = window;
    }

    public ChatConnection getConnection() {
        return connection;
    }

    public void setConnection(ChatConnection connection) {

        this.connection = connection;
    }
}
