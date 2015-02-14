import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hugiasgeirsson on 08/02/15.
 */
public class ChatServerDaemon extends Thread{
    ServerSocket serverSocket;
    ChatSession session;
    Map<String, ChatSession> subSessionHashMap;
    Boolean done = false;

    public ChatServerDaemon(ServerSocket serverSocket, ChatSession session) {
        this.serverSocket = serverSocket;
        this.session = session;
        this.subSessionHashMap = new HashMap<String, ChatSession>();
        subSessionHashMap.put(session.getChatName(), session);
    }

    public void disconnectAllSessions(){
        Object[] sessions = subSessionHashMap.values().toArray();
        for(Object session : sessions){
            ((ChatSession) session).disconnectFromSession();
            ((ChatSession) session).getWindow().dispose();
        }
    }

    public void killServerDaemon(){
        try {
            serverSocket.close();
            System.out.println("Daemon dead!");
        } catch (IOException e){
            System.out.println("Can't kill daemon!  " + e);
        }
        done = true;
    }

    public void run() {
        while (!done) {
            Socket clientSocket = null;
            try {
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.out.println("Accept failed: 4444");
                done = true;
            }
            if(!done) {
                ChatConnectionManager connectionManager = new ChatConnectionManager(this, clientSocket);
                ChatConnectionManagerWindow connectionManagerWindow =
                        new ChatConnectionManagerWindow(connectionManager);
                String connectRequestMessage = connectionManager.getServerConnection().getRequestMessage();
                connectionManagerWindow.setRequestMessage("Request from " + connectRequestMessage);
            }
        }
    }
}
