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
    private boolean connected = false;
    private PrintWriter out;
    private BufferedReader in;
    private boolean done = false;
    private boolean primitiveConnection;


    public ChatConnection(Socket clientSocket, ChatSession session){
        this.clientSocket = clientSocket;
        this.session = session;
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

        try{
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
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
                String buffer = in.readLine();
                System.out.println(buffer);
                if(buffer.startsWith("<message")){
                    while (!buffer.endsWith("</message>")){
                        buffer = buffer + in.readLine();
                    }
                }
                ChatMessage message = session.encoderDecoder.xmlToChatMessage(buffer);
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


            }catch(IOException e){
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
