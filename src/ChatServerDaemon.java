import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by hugiasgeirsson on 08/02/15.
 */
public class ChatServerDaemon extends Thread{
    ServerSocket serverSocket;
    ChatSession session;
    ChatWindow window;

    public ChatServerDaemon(ServerSocket serverSocket, ChatSession session, ChatWindow window) {
        this.serverSocket = serverSocket;
        this.session = session;
        this.window = window;
    }
    public void run() {
        while (true) {
            Socket clientSocket = null;
            try {
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.out.println("Accept failed: 4444");
                System.exit(-1);
            }
            ChatConnection serverConnection = new ChatConnection(clientSocket, session);
            serverConnection.start();
            session.setConnection(serverConnection);
        }
    }
}
