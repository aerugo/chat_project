import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by hugiasgeirsson on 04/02/15.
 */

public class ChatAppMainWindow extends JFrame implements ActionListener{
    JButton newClientButton;
    JButton newServerButton;
    JTextField hostAddress;
    JTextField connectionPort;
    JTextField userName;
    JTextField chatName;
    JTextField connectRequestMessage;

    public ChatAppMainWindow(){
        super("Amazochat");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(300, 300));

        newClientButton = new JButton("New Client");
        newClientButton.addActionListener(this);
        newServerButton = new JButton("New Server");
        newServerButton.addActionListener(this);
        hostAddress = new JTextField("localhost");
        connectionPort = new JTextField("4444");
        userName = new JTextField("User");
        chatName = new JTextField("MyChatName");
        connectRequestMessage = new JTextField("Request message");

        setLayout(new GridLayout(3,3));
        add(newClientButton);
        add(newServerButton);
        add(hostAddress);
        add(connectionPort);
        add(userName);
        add(chatName);
        add(connectRequestMessage);

        pack();
        setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == newClientButton){
            ChatSession session = new ChatSession(
                    hostAddress.getText(),
                    Integer.parseInt(connectionPort.getText()),
                    userName.getText(),
                    chatName.getText());
            session.setConnectRequestMessage(connectRequestMessage.getText());
            new ChatSessionWindow(session);
        }
        if(e.getSource() == newServerButton){
            ChatSession session = new ChatSession(
                    "server",
                    Integer.parseInt(connectionPort.getText()),
                    userName.getText(),
                    chatName.getText());
            new ChatSessionWindow(session);
        }
    }

    public static void main(String[] args){
        new ChatAppMainWindow();
    }
}
