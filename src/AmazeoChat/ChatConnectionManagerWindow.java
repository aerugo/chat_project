package AmazeoChat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by hugiasgeirsson on 10/02/15.
 */
public class ChatConnectionManagerWindow extends JFrame implements ActionListener{
    JButton startNewChat;
    JButton refuseConnection;
    JTextField newChatName;
    JLabel newChatNameLabel;
    JTextField requestMessage;
    JLabel chatListLabel;
    JButton addToChat;
    JComboBox<Object> chatList;
    ChatConnectionManager connectionManager;
    JPanel nestedPanel;

    public ChatConnectionManagerWindow(ChatConnectionManager connectionManager){

        this.setTitle("Manage incoming connection");
        setResizable(false);

        Dimension buttonDimensions = new Dimension(50, 20);

        this.connectionManager = connectionManager;

        nestedPanel = new JPanel();
        startNewChat = new JButton("Start new chat");
        startNewChat.setSize(buttonDimensions);
        startNewChat.addActionListener(this);
        addToChat = new JButton("Add to chat");
        addToChat.setSize(buttonDimensions);
        addToChat.addActionListener(this);
        refuseConnection = new JButton("Refuse connection");
        refuseConnection.setSize(buttonDimensions);
        refuseConnection.addActionListener(this);
        requestMessage = new JTextField("No request message");
        requestMessage.setEditable(false);
        newChatName = new JTextField("New Amazochat");
        newChatNameLabel = new JLabel("New chat name:");
        chatList = new JComboBox(connectionManager.getOpenSessions());
        chatListLabel = new JLabel("Active chats:");

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLayout(new BorderLayout());
        add(requestMessage, BorderLayout.NORTH);
        add(nestedPanel, BorderLayout.SOUTH);
        nestedPanel.setLayout(new GridLayout(3, 3));
        nestedPanel.add(startNewChat);
        nestedPanel.add(newChatNameLabel);
        nestedPanel.add(newChatName);
        nestedPanel.add(addToChat);
        nestedPanel.add(chatListLabel);
        nestedPanel.add(chatList);
        nestedPanel.add(refuseConnection);

        pack();
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == startNewChat){
            connectionManager.startNewSession(newChatName.getText());
            dispose();
        }
        if(e.getSource() == addToChat){
            connectionManager.connectToActiveSession(chatList.getSelectedItem().toString());
            dispose();
        }
        if(e.getSource() == refuseConnection){
            connectionManager.refuseConnection();
            dispose();
        }
    }

    public void setRequestMessage(String requestMessageString) {
        this.requestMessage.setText(requestMessageString);
    }
}
