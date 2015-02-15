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
    JLabel hostAddressLabel;
    JTextField connectionPort;
    JLabel connectionPortLabel;
    JTextField userName;
    JLabel userNameLabel;
    JTextField chatName;
    JLabel chatNameLabel;
    JTextField connectRequestMessage;
    JLabel connectRequestLabel;

    public ChatAppMainWindow(){
        super("Amazochat");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        setLayout(new GridLayout(3,3));
        Dimension buttonDimensions = new Dimension(50, 20);

        newClientButton = new JButton("New Client");
        newClientButton.addActionListener(this);
        newClientButton.setPreferredSize(buttonDimensions);
        newServerButton = new JButton("New Server");
        newServerButton.addActionListener(this);
        newServerButton.setPreferredSize(buttonDimensions);
        hostAddress = new JTextField("localhost");
        hostAddress.setPreferredSize(buttonDimensions);
        hostAddressLabel = new JLabel("Host IP");
        connectionPort = new JTextField("4444");
        connectionPort.setPreferredSize(buttonDimensions);
        connectionPortLabel = new JLabel("Port:");
        userName = new JTextField("User");
        userName.setPreferredSize(buttonDimensions);
        userNameLabel = new JLabel("User name:");
        chatName = new JTextField("MyChatName");
        chatName.setPreferredSize(buttonDimensions);
        chatNameLabel = new JLabel("Chat session name");
        connectRequestMessage = new JTextField("Request message");
        connectRequestLabel = new JLabel("Request message");

        add(newClientButton);
        add(newServerButton);
        add(userNameLabel);
        add(userName);

        add(hostAddressLabel);
        add(hostAddress);
        add(connectionPortLabel);
        add(connectionPort);

        add(chatNameLabel);
        add(chatName);
        add(connectRequestLabel);
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
