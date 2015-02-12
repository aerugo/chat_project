import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by hugiasgeirsson on 11/02/15.
 */
public class ChatServerDaemonWindow extends JFrame implements ActionListener{
    JButton disconnectServer;
    ChatServerDaemon serverDaemon;

    public ChatServerDaemonWindow(ChatServerDaemon serverDaemon){
        this.serverDaemon = serverDaemon;
        this.setTitle("Server listening on " + serverDaemon.serverSocket.getLocalPort());
        disconnectServer = new JButton("Disconnect server");
        disconnectServer.addActionListener(this);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setPreferredSize(new Dimension(300, 300));

        setLayout(new GridLayout(2,2));
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
