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
    private String requestMessage;
    private PrintWriter out;
    private BufferedReader in;
    private boolean done;


    public ChatConnection(Socket clientSocket, ChatSession session){
        this.clientSocket = clientSocket;
        this.session = session;
        this.done = false;
    }

    public void sendMessage(ChatMessage message){
        String xmlMessage = session.encoderDecoder.chatMessageToXML(message);
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

    public void sendFile(File file){

    }

    public void acceptFile(File file){

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

        // Kommer vi hit gick anslutningen bra.
        // Vi skriver ut IP-nummret från klienten
        System.out.println("Connection Established: "
                + clientSocket.getInetAddress());

        //Client request pending
        if(!session.getHostAddress().equals("server")) {
            sendMessage(new ChatMessage(session.getUserName() + ": " + session.getConnectRequestMessage(), "request"));
            boolean pending = true;
            while (pending) {

                ChatMessage message = getMessageFromBuffer();

                if (message.getRequestAnswer().equals("yes")) {
                    sendMessage(new ChatMessage(session.getUserName(), new Color(0, 255, 0), "has connected!", "message"));
                } else {
                    done = true;
                }

                pending = false;
            }
        }

        //Listening for messages and username updates
        while(!done){

            ChatMessage message = getMessageFromBuffer();

            String echo = "Recieved: ("
                    + clientSocket.getInetAddress()
                    + ") ";

            if(message.getMessageType().equals("request")) {
                this.requestMessage = message.getMessageString();
            }

            if(message.getMessageType().equals("message")) {
                session.getWindow().printMessage(message);

                if (session.getHostAddress().equals("server") & !done) {
                    session.sendMessageToAll(message);
                }
            }

            System.out.println(echo);

            try{
                connectedUserName = message.getMessageAuthor();
            }catch(NullPointerException e){
                System.out.println("No name available");
                connectedUserName = "Unknown";
            }
        }

        // Reach here when connection is done

        disconnect();
        session.removeConnectionFromUserChooser(this);
        System.out.println(this + " disconnected!");
    }

    private void disconnect(){
        try{
            in.close();
            out.close();
            clientSocket.close();
            session.getConnectionList().remove(this);
        }catch(Exception e){
        }
    }

    private ChatMessage getMessageFromBuffer(){
        String buffer = "";
        ChatMessage message;
        String messageType = "unknown";

        try {
            while (messageType.equals("unknown")){
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
            }
            while (!buffer.endsWith("</"+messageType+">")) {
                buffer = buffer + in.readLine();
            }
            message = session.encoderDecoder.xmlToChatMessage(buffer);
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

    public void setSession(ChatSession session) {
        this.session = session;
    }

    public String getRequestMessage() {
        return requestMessage;
    }

    @Override
    public String toString() {
        return connectedUserName;
    }
}
