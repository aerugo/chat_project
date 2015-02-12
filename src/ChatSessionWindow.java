/**
 * Created by hugiasgeirsson on 05/02/15.
 */

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;


public class ChatSessionWindow extends JFrame implements ActionListener{
    private JTextPane displayPane;
    private JTextArea editorPane;
    private JButton sendButton;
    private JButton disconnectButton;
    private ChatSession chatSession;
    private JButton colorChooser;
    private JButton kickUser;
    private JComboBox userChooser;

    public ChatSessionWindow(ChatSession session) {
        super("Amazochat");

        this.chatSession = session;
        this.chatSession.setWindow(this);

        String windowType;
        if(session.getHostAddress().equals("server")) {
            windowType = "Server - ";
        }else{
            windowType = "Client - ";
        }
        this.setTitle(windowType + session.getChatName());

        setPreferredSize(new Dimension(300, 600));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        JPanel chatPanel = new JPanel();
        displayPane = new JTextPane();
        JScrollPane displayScrollPane = new JScrollPane(displayPane);
        displayPane.setPreferredSize(new Dimension(300, 300));
        displayPane.setEditable(false);
        editorPane = new JTextArea();
        editorPane.setLineWrap(true);
        editorPane.setPreferredSize(new Dimension(300, 200));
        JScrollPane editorScrollPane = new JScrollPane(editorPane);
        sendButton = new JButton("Send");
        sendButton.addActionListener(this);
        disconnectButton = new JButton("Disconnect");
        disconnectButton.addActionListener(this);
        colorChooser = new JButton("Text color");
        colorChooser.addActionListener(this);
        kickUser = new JButton("Kick user");
        kickUser.addActionListener(this);
        userChooser = new JComboBox(session.getUserChooserModel());

        add(chatPanel);
        chatPanel.setPreferredSize(new Dimension(300, 600));
        GroupLayout layout = new GroupLayout(chatPanel);
        chatPanel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        JPanel dividerPanel = new JPanel();
        dividerPanel.setPreferredSize(new Dimension(300, 25));

        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addComponent(displayScrollPane)
                        .addComponent(dividerPanel)
                        .addComponent(editorScrollPane)
                        .addComponent(sendButton)
                        .addComponent(disconnectButton)
                        .addComponent(colorChooser)
                        .addComponent(kickUser)
                        .addComponent(userChooser)
        );

        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addComponent(displayScrollPane)
                        .addComponent(dividerPanel)
                        .addComponent(editorScrollPane)
                        .addComponent(sendButton)
                        .addComponent(disconnectButton)
                        .addComponent(colorChooser)
                        .addComponent(kickUser)
                        .addComponent(userChooser)
        );

        pack();
        setVisible(true);

        if(!session.getHostAddress().equals("server")){
            kickUser.setVisible(false);
            userChooser.setVisible(false);
        }

        if(!session.connected & !session.getHostAddress().equals("server")){
            printError("Connection to server failed! Disconnect to close window and try different connection.");
        }
    }

    public void printMessage(ChatMessage message){
        StyledDocument chatLog = displayPane.getStyledDocument();
        Style messageStyle = chatLog.addStyle("Message", null);
        Style authorStyle = chatLog.addStyle("User", null);
        messageStyle.addAttribute(StyleConstants.Foreground, message.getMessageColor());
        try {
            chatLog.insertString(chatLog.getLength(), message.getMessageAuthor() + ": ", authorStyle);
            chatLog.insertString(chatLog.getLength(), message.getMessageXML() + "\n", messageStyle);
        } catch (BadLocationException error) {
            System.err.println("IndexOutOfBoundsException: " + error.getMessage());
        }
    }

    public void printError(String errorMessage){
        StyledDocument chatLog = displayPane.getStyledDocument();
        Style messageStyle = chatLog.addStyle("Message", null);
        Style authorStyle = chatLog.addStyle("User", null);
        messageStyle.addAttribute(StyleConstants.Foreground, Color.red);
        try {
            chatLog.insertString(chatLog.getLength(), "System" + ": ", authorStyle);
            chatLog.insertString(chatLog.getLength(), errorMessage + "\n", messageStyle);
        } catch (BadLocationException error) {
            System.err.println("IndexOutOfBoundsException: " + error.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == sendButton & !editorPane.getText().equals("")){
            ChatMessage myMessage = chatSession.inputToChatMessage(chatSession.getUserName(),
                    chatSession.getMessageColor(), editorPane.getText());
            if(chatSession.getHostAddress().equals("server")){
                this.printMessage(myMessage);
            }
            if(!chatSession.getConnectionList().isEmpty()){
                chatSession.sendMessageToAll(myMessage);
            }
            editorPane.setText("");
        }
        if(e.getSource() == colorChooser){
            Color newColor = JColorChooser.showDialog(
                    ChatSessionWindow.this,
                    "Choose Text Color",
                    chatSession.getMessageColor());
            if(newColor != null){
                chatSession.setMessageColor(newColor);
            }
        }
        if(e.getSource() == disconnectButton){
            if(!chatSession.getConnectionList().isEmpty()){
                chatSession.disconnectFromSession();
            }
            this.dispose();
        }

        if(e.getSource() == kickUser){
            if(!chatSession.getConnectionList().isEmpty()){
                ChatConnection connection = (ChatConnection) userChooser.getSelectedItem();
                chatSession.sendMessageToAll(new ChatMessage(
                        chatSession.getUserName(),
                        Color.red,
                        "has KICKED user " + connection.getConnectedUserName(),
                        "message"));
                connection.killConnection();
            }
        }

    }

}
