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
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private ChatMessage message;
    private String echo;

    public ChatConnection(Socket socket, ChatSession session){
        this.socket = socket;
        this.session = session;
    }

    public void sendMessage(ChatMessage message){
        try{
            out.writeObject(message);
            out.flush();
            System.out.println("Sent message");
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
        boolean done = false;

        // Anslut läs- och skrivströmmarna
        try{
            out = new ObjectOutputStream(
                    this.socket.getOutputStream());
        }catch(IOException e){
            System.out.println("getOutputStream failed: " + e);
            System.exit(1);
        }
        try{
            in = new ObjectInputStream(this.socket.getInputStream());
        }catch(IOException e){
            System.out.println("getInputStream failed: " + e);
            System.exit(1);
        }

        // Kommer vi hit gick anslutningen bra.
        // Vi skriver ut IP-nummret från klienten
        System.out.println("Connection Established: "
                + socket.getInetAddress());

        while(!done){
            try{
                System.out.println("pass0");
                message = (ChatMessage) in.readObject();
                System.out.println("pass1");
                if(message.disconnectFlag){
                    System.out.println("Client disconnect!");
                    System.out.println("pass2");
                    done = true;
                }else{
                    echo = "Recieved: ("
                            + socket.getInetAddress()
                            + ") ";
                    session.getWindow().printMessage(message);
                    System.out.println(echo);
                }
                System.out.println("pass3");
            }catch(Exception e){
                System.out.println("read failed: " + e);
                System.exit(1);
            }
        }

        try{
            in.close();
            out.close();
            socket.close();
        }catch(Exception e){}
    }

}
