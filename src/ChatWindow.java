/**
 * Created by hugiasgeirsson on 05/02/15.
 */

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.Style;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class ChatWindow extends JFrame implements ActionListener{
    private JTextPane displayPane;
    private JTextArea editorPane;
    private JButton sendButton;
    private JButton disconnectButton;
    private ChatSession chatSession;
    private JButton colorChooser;

    public void setServerConnection(ChatConnection serverConnection) {
        this.serverConnection = serverConnection;
    }

    private ChatConnection serverConnection = null;
    private ChatConnection clientConnection = null;

    public ChatWindow(ChatSession session) {
        super("Amazochat");

        this.chatSession = session;
        this.chatSession.setWindow(this);
        this.chatSession.establishConnection();

        setPreferredSize(new Dimension(300, 600));

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
        colorChooser = new JButton("Text color");
        colorChooser.addActionListener(this);

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
        );

        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addComponent(displayScrollPane)
                        .addComponent(dividerPanel)
                        .addComponent(editorScrollPane)
                        .addComponent(sendButton)
                        .addComponent(disconnectButton)
                        .addComponent(colorChooser)
        );

        pack();
        setVisible(true);
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

    public static void main(String[] args){
        //ChatWindow server = new ChatWindow(new ChatSession("server", 4444, "Master"));
        //ChatWindow client = new ChatWindow(new ChatSession("192.168.0.103", 4444, "Slave"));
    }

    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == sendButton & !editorPane.getText().equals("")){
            ChatMessage myMessage = chatSession.inputToChatMessage(chatSession.getUserName(),
                    chatSession.getMessageColor(), editorPane.getText());
            this.printMessage(myMessage);
            chatSession.getConnection().sendMessage(myMessage);
            editorPane.setText("");
        }
        if(e.getSource() == colorChooser){
            Color newColor = JColorChooser.showDialog(
                    ChatWindow.this,
                    "Choose Text Color",
                    chatSession.getMessageColor());
            if(newColor != null){
                chatSession.setMessageColor(newColor);
            }
        }
    }

}
