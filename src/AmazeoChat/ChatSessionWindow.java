package AmazeoChat; /**
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
    private JButton colorChooserButton;
    private JButton kickUser;
    private JButton sendFileButton;
    private JButton requestKeyButton;
    private JButton encryptionChooserButton;
    private JComboBox encryptionChooser;
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

        setPreferredSize(new Dimension(300, 700));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        Dimension buttonSize = new Dimension(50, 20);

        JPanel chatPanel = new JPanel();
        displayPane = new JTextPane();
        JScrollPane displayScrollPane = new JScrollPane(displayPane);
        displayPane.setPreferredSize(new Dimension(300, 300));
        displayPane.setEditable(false);
        editorPane = new JTextArea();
        editorPane.setLineWrap(true);
        editorPane.setPreferredSize(new Dimension(300, 100));
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
        requestKeyButton = new JButton("Request key");
        requestKeyButton.addActionListener(this);
        encryptionChooserButton = new JButton("Set encryption");
        encryptionChooserButton.addActionListener(this);

        encryptionChooser = new JComboBox(session.encryptDecrypt.getKnownTypesModel());
        encryptionChooser.addActionListener(this);
        userChooser = new JComboBox(session.getUserChooserModel());

        add(chatPanel);
        chatPanel.setPreferredSize(new Dimension(300, 600));
        GroupLayout layout = new GroupLayout(chatPanel);
        chatPanel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        JPanel messagePanel = new JPanel();
        messagePanel.setSize(new Dimension(300, 33));
        messagePanel.setLayout(new GridLayout(2, 2));
        messagePanel.add(colorChooserButton);
        messagePanel.add(sendButton);
        messagePanel.add(encryptionChooser);
        messagePanel.add(encryptionChooserButton);

        JPanel userInteractionPanel = new JPanel();
        userInteractionPanel.setLayout(new GridLayout(3, 2));
        userInteractionPanel.setSize(new Dimension(300, 50));
        JLabel selectUserLabel = new JLabel("Select user:");
        selectUserLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        userInteractionPanel.add(selectUserLabel);
        userInteractionPanel.add(userChooser);
        userInteractionPanel.add(sendFileButton);
        userInteractionPanel.add(requestKeyButton);
        userInteractionPanel.add(new JLabel(""));
        userInteractionPanel.add(kickUser);

        JPanel disconnectPanel = new JPanel();
        disconnectPanel.setLayout(new GridLayout(1, 2));
        disconnectPanel.setSize(new Dimension(300, 17));
        JLabel endSessionLabel = new JLabel("End Session:");
        endSessionLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        disconnectPanel.add(endSessionLabel);
        disconnectPanel.add(disconnectButton);

        JPanel controlTopPanel = new JPanel();
        controlTopPanel.setLayout(new BorderLayout());
        controlTopPanel.add(messagePanel, BorderLayout.NORTH);
        controlTopPanel.add(new JSeparator(), BorderLayout.SOUTH);

        JPanel controlCenterPanel = new JPanel();
        controlCenterPanel.setLayout(new BorderLayout());
        controlCenterPanel.add(userInteractionPanel, BorderLayout.NORTH);
        controlCenterPanel.add(new JSeparator(), BorderLayout.SOUTH);

        JPanel controlBottomPanel = new JPanel();
        controlBottomPanel.setLayout(new BorderLayout());
        controlBottomPanel.add(disconnectPanel, BorderLayout.NORTH);

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BorderLayout());
        controlPanel.add(controlTopPanel, BorderLayout.NORTH);
        controlPanel.add(controlCenterPanel, BorderLayout.CENTER);
        controlPanel.add(controlBottomPanel, BorderLayout.SOUTH);

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
            this.printMessage(myMessage);
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
                ChatConnection connection = (ChatConnection) userChooser.getSelectedItem();
                chatSession.sendMessageToAll(new ChatMessage(
                        chatSession.getUserName(),
                        new Color(255,0,0),
                        "has KICKED user " + connection.getConnectedUserName(),
                        "message"));
                connection.killConnection();
            }
        }

        if(e.getSource() == sendFileButton){
            new ChatFileTransferSendWindow((ChatConnection) userChooser.getSelectedItem());
        }

        if(e.getSource() == encryptionChooserButton){
            chatSession.setSessionEncryption(encryptionChooser.getSelectedItem().toString());
        }

        if(e.getSource() == requestKeyButton){
            ChatConnection chosenConnection = (ChatConnection) userChooser.getSelectedItem();
            new ChatKeyRequestWindow(chosenConnection);
        }

    }

}
