import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by hugiasgeirsson on 04/02/15.
 */

public class ChatAppMainMenuWindow extends JFrame implements ActionListener{
    JButton newClientButton;
    JButton newServerButton;
    JTextField hostAddress;
    JTextField connectionPort;
    JTextField userName;
    JTextField chatName;

    public ChatAppMainMenuWindow(){
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

        setLayout(new GridLayout(3,2));
        add(newClientButton);
        add(newServerButton);
        add(hostAddress);
        add(connectionPort);
        add(userName);
        add(chatName);

        pack();
        setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == newClientButton){
            new ChatSessionWindow(new ChatSession(
                    hostAddress.getText(),
                    Integer.parseInt(connectionPort.getText()),
                    userName.getText(),
                    chatName.getText()));
        }
        if(e.getSource() == newServerButton){
            new ChatSessionWindow(new ChatSession(
                    "server",
                    Integer.parseInt(connectionPort.getText()),
                    userName.getText(),
                    chatName.getText()));
        }
    }

    public static void main(String[] args){
        new ChatAppMainMenuWindow();
    }
}
