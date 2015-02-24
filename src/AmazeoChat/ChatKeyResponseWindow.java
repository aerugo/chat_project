package AmazeoChat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Created by hugiasgeirsson on 20/02/15.
 */
public class ChatKeyResponseWindow extends JFrame implements ActionListener{
    ChatConnection connection;
    JButton sendKeyButton;
    JButton refuseButton;
    String keyRequestType;

    public ChatKeyResponseWindow(ChatConnection connection, ChatMessage keyRequest){
        this.connection = connection;

        setTitle("Key request");
        setResizable(false);
        this.keyRequestType = keyRequest.getKeyRequestType();
        ArrayList<String> knownEncryptionTypes = connection.getSession().encryptDecrypt.getKnownTypes();

        refuseButton = new JButton("Refuse");
        refuseButton.addActionListener(this);
        sendKeyButton = new JButton("Send key");
        sendKeyButton.addActionListener(this);

        setLayout(new BorderLayout());
        add(new JLabel("Keyrequest from user " + connection.getConnectedUserName() + "\n " +
                "For key type " + keyRequestType), BorderLayout.NORTH);
        JPanel controls = new JPanel(new GridLayout(1,2));
        controls.add(refuseButton);
        controls.add(sendKeyButton);

        if(!knownEncryptionTypes.contains(keyRequestType)){
            sendKeyButton.setEnabled(false);
            sendKeyButton.setText("Unknown type");
        }

        add(controls, BorderLayout.SOUTH);

        pack();
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == refuseButton){
            this.dispose();
        }
        if(e.getSource() == sendKeyButton){
            String key = "";
            if(keyRequestType.equals("AES")){
                key = connection.getSession().encryptDecrypt.getKeyString("AES");
            }
            if(keyRequestType.equals("caesar")){
                key = connection.getSession().encryptDecrypt.getKeyString("caesar");
            }
            connection.sendKeyResponse(key);
            this.dispose();
        }
    }
}
