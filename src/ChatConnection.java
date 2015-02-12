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
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean done = false;
    private boolean primitiveConnection;


    public ChatConnection(Socket clientSocket, ChatSession session){
        this.clientSocket = clientSocket;
        this.session = session;
    }

    public void sendMessage(ChatMessage message){
        String xmlMessage = session.encoderDecoder.chatMessageToXML(message);
        try{
            out.writeObject(message);
            out.flush();
            System.out.println("Sent message");
            if(message.getMessageType().equals("disconnect")){
                this.done = true;
            }
        }catch(IOException e){
            System.out.println("read failed: " + e);
            session.getWindow().printError("read failed: " + e);
        }
    }

    public void run(){

        // Connect handshake streams


        // Connect object input/output to Amazochat-client
        try{
            out = new ObjectOutputStream(
                    this.clientSocket.getOutputStream());
        }catch(Exception e){
            System.out.println("getOutputStream failed: " + e);
            return;
        }
        try{
            in = new ObjectInputStream(this.clientSocket.getInputStream());
        }catch(Exception e){
            System.out.println("getInputStream failed: " + e);
            session.getWindow().printError("Connection not established");
            return;
        }

        // Kommer vi hit gick anslutningen bra.
        // Vi skriver ut IP-nummret från klienten
        System.out.println("Connection Established: "
                + clientSocket.getInetAddress());

        sendMessage(new ChatMessage(session.getUserName(), new Color(0, 255, 0), "has connected!", "message"));

        while(!this.done){
            try{
                ChatMessage message = (ChatMessage) in.readObject();
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
                connectedUserName = message.getMessageAuthor();

            }catch(Exception e){
                System.out.println( this + " read failed: " + e);
                done = true;
            }
        }
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

    public void killConnection(){
        done = true;
    }

    public String getConnectedUserName() {
        return connectedUserName;
    }

    public void setConnectedUserName(String connectedUserName) {
        this.connectedUserName = connectedUserName;
    }

    @Override
    public String toString() {
        return connectedUserName;
    }
}
