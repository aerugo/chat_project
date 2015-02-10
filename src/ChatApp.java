import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by hugiasgeirsson on 04/02/15.
 */

public class ChatApp extends JFrame implements ActionListener{
    JButton newClientButton;
    JButton newServerButton;
    JTextField hostAddress;
    JTextField connectionPort;
    JTextField userName;

    public ChatApp(){
        super("Amazochat");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(300, 300));

        JPanel chatPanel = new JPanel();
        newClientButton = new JButton("New Client");
        newClientButton.addActionListener(this);
        newServerButton = new JButton("New Server");
        newServerButton.addActionListener(this);
        hostAddress = new JTextField("Host IP");
        connectionPort = new JTextField("Port");
        userName = new JTextField("User");

        setLayout(new GridLayout(3,2));
        add(newClientButton);
        add(newServerButton);
        add(hostAddress);
        add(connectionPort);
        add(userName);

        pack();
        setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == newClientButton){
            ChatWindow client = new ChatWindow(new ChatSession(
                    hostAddress.getText(),
                    Integer.parseInt(connectionPort.getText()),
                    userName.getText()));
        }
        if(e.getSource() == newServerButton){
            ChatWindow server = new ChatWindow(new ChatSession(
                    "server",
                    Integer.parseInt(connectionPort.getText()),
                    userName.getText()));
        }
    }

    public static void main(String[] args){
        new ChatApp();
    }
}
