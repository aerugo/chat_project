import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by hugiasgeirsson on 10/02/15.
 */
public class ChatConnection extends Thread{
    private Socket socket;
    private ChatSession session;
    private String connectedUserName;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private ChatMessage message;
    private String echo;
    private boolean done = false;


    public ChatConnection(Socket socket, ChatSession session){
        this.socket = socket;
        this.session = session;
    }

    public void sendMessage(ChatMessage message){
        try{
            out.writeObject(message);
            out.flush();
            System.out.println("Sent message");
            if(message.getMessageType().equals("disconnect")){
                this.done = true;
            }
        }catch(IOException e){
            System.out.println("read failed: " + e);
            System.exit(1);
        }
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public void run(){
        // Vi kör tills vi är klara

        // Anslut läs- och skrivströmmarna
        try{
            out = new ObjectOutputStream(
                    this.socket.getOutputStream());
        }catch(Exception e){
            System.out.println("getOutputStream failed: " + e);
            return;
        }
        try{
            in = new ObjectInputStream(this.socket.getInputStream());
        }catch(Exception e){
            System.out.println("getInputStream failed: " + e);
            return;
        }

        // Kommer vi hit gick anslutningen bra.
        // Vi skriver ut IP-nummret från klienten
        System.out.println("Connection Established: "
                + socket.getInetAddress());

        sendMessage(new ChatMessage(session.getUserName(), Color.green, "has connected!", "message"));

        while(!this.done){
            try{
                message = (ChatMessage) in.readObject();
                echo = "Recieved: ("
                        + socket.getInetAddress()
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
            socket.close();
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
