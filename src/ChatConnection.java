import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by hugiasgeirsson on 10/02/15.
 */
public class ChatConnection extends Thread{
    private Socket clientSocket;
    private ChatSession session;
    private String connectedUserName;
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
                this.done = true;
            }
        }catch(Exception e){
            System.out.println("read failed: " + e);
            session.getWindow().printError("read failed: " + e);
        }
    }

    public void run(){

        try{
            out = new PrintWriter(clientSocket.getOutputStream(), true);
        }catch(Exception e){
            System.out.println("getOutputStream failed: " + e);
            return;
        }

        if(session.getHostAddress().equals("server")){System.out.println("Server out ok");}

        try{
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        }catch(Exception e){
            System.out.println("getInputStream failed: " + e);
            session.getWindow().printError("Connection not established");
            return;
        }

        if(session.getHostAddress().equals("server")){System.out.println("Server in ok");}

        // Kommer vi hit gick anslutningen bra.
        // Vi skriver ut IP-nummret från klienten
        System.out.println("Connection Established: "
                + clientSocket.getInetAddress());

        // Wait for request message

        if(session.getHostAddress().equals("server")){System.out.println("Server established");}

        //Client request pending
        if(!session.getHostAddress().equals("server")) {
            sendMessage(new ChatMessage(session.getUserName() + "Wants to connect!", "request"));
            boolean pending = true;
            while (pending) {

                ChatMessage message = getMessageFromBuffer("request");

                if (message.getRequestAnswer().equals("yes")) {
                    sendMessage(new ChatMessage(session.getUserName(), new Color(0, 255, 0), "has connected!", "message"));
                } else {
                    done = true;
                }

                pending = false;
            }
        }

        if(session.getHostAddress().equals("server")){System.out.println("Server prepares to listen");}

        //Listening for messages and username updates

        while(!done){

            if(!session.getHostAddress().equals("server")){System.out.println("Client running");}
            if(session.getHostAddress().equals("server")){System.out.println("Server running");}

            ChatMessage message = getMessageFromBuffer("message");

            String echo = "Recieved: ("
                    + clientSocket.getInetAddress()
                    + ") ";

            if(message.getMessageType().equals("message")) {
                session.getWindow().printMessage(message);

                if (session.getHostAddress().equals("server")) {
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

        System.out.println("disconnect");
        disconnect();
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

    private ChatMessage getMessageFromBuffer(String messageType){
        String buffer = "";
        ChatMessage message;
        try {
            while (!buffer.startsWith("<"+messageType)){
                buffer = in.readLine();
            }
            System.out.println(buffer);
            while (!buffer.endsWith("</"+messageType+">")) {
                buffer = buffer + in.readLine();
            }
            message = session.encoderDecoder.xmlToChatMessage(buffer);
        }catch(IOException e){
            System.out.println( this + " read failed: " + e);
            message = new ChatMessage("System",new Color(255,0,0),"Could not get message...","message");
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
        return "Primitive client - no request message.";
    }

    @Override
    public String toString() {
        return connectedUserName;
    }
}
