/**
 * Created by hugiasgeirsson on 05/02/15.
 */

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;


public class ChatSessionWindow extends JFrame implements ActionListener{
    private JPanel controlPanel;
    private JTextPane displayPane;
    private JTextArea editorPane;
    private JButton sendButton;
    private JButton disconnectButton;
    private ChatSession chatSession;
    private JButton colorChooserButton;
    private JButton kickUser;
    private JButton sendFileButton;
    private JButton encryptionOptionsButton;
    private JComboBox kickUserChooser;
    private JComboBox sendFileUserChooser;

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

        setPreferredSize(new Dimension(300, 700));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        Dimension buttonSize = new Dimension(50, 20);

        JPanel chatPanel = new JPanel();
        controlPanel = new JPanel();
        displayPane = new JTextPane();
        JScrollPane displayScrollPane = new JScrollPane(displayPane);
        displayPane.setPreferredSize(new Dimension(300, 300));
        displayPane.setEditable(false);
        editorPane = new JTextArea();
        editorPane.setLineWrap(true);
        editorPane.setPreferredSize(new Dimension(300, 200));
        JScrollPane editorScrollPane = new JScrollPane(editorPane);
        sendButton = new JButton("Send");
        sendButton.setSize(buttonSize);
        sendButton.addActionListener(this);
        disconnectButton = new JButton("Disconnect");
        disconnectButton.setSize(buttonSize);
        disconnectButton.addActionListener(this);
        colorChooserButton = new JButton("Text color");
        colorChooserButton.setSize(buttonSize);
        colorChooserButton.addActionListener(this);
        kickUser = new JButton("Kick user");
        kickUser.setSize(buttonSize);
        kickUser.addActionListener(this);
        sendFileButton = new JButton("Send file");
        sendFileButton.addActionListener(this);
        encryptionOptionsButton = new JButton("Encryption");
        encryptionOptionsButton.addActionListener(this);
        kickUserChooser = new JComboBox(session.getUserChooserModel());
        sendFileUserChooser = new JComboBox(session.getUserChooserModel());

        add(chatPanel);
        chatPanel.setPreferredSize(new Dimension(300, 600));
        GroupLayout layout = new GroupLayout(chatPanel);
        chatPanel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        controlPanel.setLayout(new GridLayout(4,2));
        controlPanel.add(colorChooserButton);
        controlPanel.add(sendButton);
        controlPanel.add(sendFileUserChooser);
        controlPanel.add(sendFileButton);
        controlPanel.add(kickUserChooser);
        controlPanel.add(kickUser);
        controlPanel.add(encryptionOptionsButton);
        controlPanel.add(disconnectButton);

        JPanel dividerPanel = new JPanel();
        dividerPanel.setPreferredSize(new Dimension(300, 25));

        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addComponent(displayScrollPane)
                        .addComponent(dividerPanel)
                        .addComponent(editorScrollPane)
                        .addComponent(controlPanel)
        );

        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addComponent(displayScrollPane)
                        .addComponent(dividerPanel)
                        .addComponent(editorScrollPane)
                        .addComponent(controlPanel)
        );

        pack();
        setVisible(true);

        if(!session.getHostAddress().equals("server")){
            kickUser.setVisible(false);
            kickUserChooser.setVisible(false);
        }

        if(!session.connected & !session.getHostAddress().equals("server")){
            printNotification("Connection to server failed! Disconnect to close window and try different connection.");
        }
    }

    public void printMessage(ChatMessage message){
        StyledDocument chatLog = displayPane.getStyledDocument();
        Style messageStyle = chatLog.addStyle("Message", null);
        Style authorStyle = chatLog.addStyle("User", null);
        messageStyle.addAttribute(StyleConstants.Foreground, message.getMessageColor());
        try {
            chatLog.insertString(chatLog.getLength(), message.getMessageAuthor() + ": ", authorStyle);
            chatLog.insertString(chatLog.getLength(), message.getMessageString() + "\n", messageStyle);
        } catch (BadLocationException error) {
            System.err.println("IndexOutOfBoundsException: " + error.getMessage());
        }
    }

    public void printNotification(String errorMessage){
        StyledDocument chatLog = displayPane.getStyledDocument();
        Style messageStyle = chatLog.addStyle("Message", null);
        Style authorStyle = chatLog.addStyle("User", null);
        messageStyle.addAttribute(StyleConstants.Foreground, new Color(255,0,0));
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
        if(e.getSource() == colorChooserButton){
            Color newColor = JColorChooser.showDialog(
                    ChatSessionWindow.this,
                    "Choose Text Color",
                    chatSession.getMessageColor());
            if(newColor != null){
                chatSession.setMessageColor(newColor);
            }
        }
        if(e.getSource() == disconnectButton){
            if(!chatSession.getConnectionList().isEmpty() & chatSession.connected){
                chatSession.disconnectFromSession();
            }
            this.dispose();
        }

        if(e.getSource() == kickUser){
            if(!chatSession.getConnectionList().isEmpty()){
                ChatConnection connection = (ChatConnection) kickUserChooser.getSelectedItem();
                chatSession.sendMessageToAll(new ChatMessage(
                        chatSession.getUserName(),
                        new Color(255,0,0),
                        "has KICKED user " + connection.getConnectedUserName(),
                        "message"));
                connection.killConnection();
            }
        }

        if(e.getSource() == sendFileButton){
            new ChatFileTransferSendWindow((ChatConnection) sendFileUserChooser.getSelectedItem());
        }

    }

}
