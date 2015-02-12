import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by hugiasgeirsson on 10/02/15.
 */
public class ChatConnectionManagerWindow extends JFrame implements ActionListener{
    JButton startNewChat;
    JTextField newChatName;
    JButton addToChat;
    JComboBox<Object> chatList;
    ChatConnectionManager connectionManager;

    public ChatConnectionManagerWindow(ChatConnectionManager connectionManager){
        this.connectionManager = connectionManager;
        startNewChat = new JButton("Start new chat");
        startNewChat.addActionListener(this);
        addToChat = new JButton("Add to chat");
        addToChat.addActionListener(this);
        newChatName = new JTextField("New chat name");
        chatList = new JComboBox(connectionManager.getOpenSessions());

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setPreferredSize(new Dimension(300, 300));

        setLayout(new GridLayout(2,2));
        add(startNewChat);
        add(addToChat);
        add(newChatName);
        add(chatList);
        pack();
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == startNewChat){
            new ChatSessionWindow(connectionManager.startNewSession(newChatName.getText()));
            dispose();
        }
        if(e.getSource() == addToChat){
            connectionManager.connectToActiveSession(chatList.getSelectedItem().toString());
            dispose();
        }
    }
}
