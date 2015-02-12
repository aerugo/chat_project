import java.net.Socket;

/**
 * Created by hugiasgeirsson on 11/02/15.
 */
public class ChatConnectionManager {
    private ChatServerDaemon serverDaemon;
    private ChatSession chosenSession;
    private Socket clientSocket;

    public ChatConnectionManager(ChatServerDaemon serverDaemon, Socket clientSocket){
        this.serverDaemon = serverDaemon;
        this.clientSocket = clientSocket;
    }

    public void refuseConnection(){
        try{
            clientSocket.close();
        }catch (Exception e){
            System.out.println("Could not close socket! " + e);
        }
    }

    public ChatSession startNewSession(String newChatName){
        String chatName = newChatName;
        while(serverDaemon.subSessionHashMap.containsKey(chatName)){
            chatName = "New " + chatName;
        }
        chosenSession = new ChatSession(serverDaemon.serverSocket, serverDaemon.session.getUserName(), chatName);
        serverDaemon.subSessionHashMap.put(chatName, chosenSession);
        ChatConnection serverConnection = new ChatConnection(clientSocket, chosenSession);
        serverConnection.start();
        chosenSession.addConnection(serverConnection);
        return chosenSession;
    }

    public void connectToActiveSession(String sessionName){
        chosenSession = serverDaemon.subSessionHashMap.get(sessionName);
        ChatConnection serverConnection = new ChatConnection(clientSocket, chosenSession);
        serverConnection.start();
        chosenSession.addConnection(serverConnection);
    }

    public Object[] getOpenSessions(){
        Object[] openSessions = serverDaemon.subSessionHashMap.keySet().toArray();
        return openSessions;
    }
}
