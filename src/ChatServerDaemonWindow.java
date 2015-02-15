import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by hugiasgeirsson on 11/02/15.
 */
public class ChatServerDaemonWindow extends JFrame implements ActionListener{
    JButton disconnectServer;
    JLabel warningLabel;
    ChatServerDaemon serverDaemon;

    public ChatServerDaemonWindow(ChatServerDaemon serverDaemon){
        this.serverDaemon = serverDaemon;
        this.setTitle("Server listening on " + serverDaemon.serverSocket.getLocalPort());
        disconnectServer = new JButton("Disconnect server");
        warningLabel = new JLabel("<html>Warning: All chats started by this server<br> will be terminated if disconnected.</html>");
        disconnectServer.setPreferredSize(new Dimension(200, 50));
        disconnectServer.addActionListener(this);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        setLayout(new GridLayout(2,1));
        add(warningLabel);
        add(disconnectServer);

        pack();
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == disconnectServer){
            serverDaemon.disconnectAllSessions();
            serverDaemon.killServerDaemon();
            dispose();
        }
    }
}
