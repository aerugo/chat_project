import java.net.Socket;

/**
 * Created by hugiasgeirsson on 11/02/15.
 */
public class ChatConnectionManager {
    private ChatServerDaemon serverDaemon;
    private ChatSession chosenSession;
    private ChatConnection serverConnection;

    public ChatConnectionManager(ChatServerDaemon serverDaemon, Socket clientSocket){
        this.serverDaemon = serverDaemon;
        ChatSession temporarySession = new ChatSession(serverDaemon.serverSocket,
                serverDaemon.session.getUserName(), "temp");
        this.serverConnection = new ChatConnection(clientSocket, temporarySession);
        this.serverConnection.start();
    }

    public void refuseConnection(){
        try{
            serverConnection.sendMessage(new ChatMessage("Sorry","no"));
            serverConnection.killConnection();
        }catch (Exception e){
            System.out.println("Could not close socket! " + e);
        }
    }

    public void startNewSession(String newChatName) {
        String chatName = newChatName;
        while (serverDaemon.subSessionHashMap.containsKey(chatName)) {
            chatName = "New " + chatName;
        }
        chosenSession = new ChatSession(serverDaemon.serverSocket, serverDaemon.session.getUserName(), chatName);
        new ChatSessionWindow(chosenSession);
        serverDaemon.subSessionHashMap.put(chatName, chosenSession);
        chosenSession.addConnection(serverConnection);
        serverConnection.setSession(chosenSession);
        serverConnection.sendMessage(new ChatMessage("Ok!","yes"));
    }

    public void connectToActiveSession(String sessionName){
        chosenSession = serverDaemon.subSessionHashMap.get(sessionName);
        chosenSession.addConnection(serverConnection);
        serverConnection.setSession(chosenSession);
        serverConnection.sendMessage(new ChatMessage("Ok!","yes"));
    }

    public Object[] getOpenSessions(){
        Object[] openSessions = serverDaemon.subSessionHashMap.keySet().toArray();
        return openSessions;
    }

    public ChatConnection getServerConnection() {
        return serverConnection;
    }
}
