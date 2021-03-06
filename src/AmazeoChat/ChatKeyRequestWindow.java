package AmazeoChat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.TimeUnit;

/**
 * Created by hugiasgeirsson on 20/02/15.
 */
public class ChatKeyRequestWindow extends JFrame implements ActionListener{
    ChatConnection connection;
    JTextField requestStatus;
    JButton closeWindowButton;
    JButton sendRequestButton;
    JComboBox encryptionTypeChooser;
    JTextField requestMessage;
    String requestType;

    public ChatKeyRequestWindow(ChatConnection connection){
        setTitle("Sending key request");
        setResizable(false);

        this.connection = connection;
        closeWindowButton = new JButton("Close");
        closeWindowButton.addActionListener(this);
        sendRequestButton = new JButton("Request");
        sendRequestButton.addActionListener(this);
        encryptionTypeChooser = new JComboBox(connection.getSession().encryptDecrypt.getKnownTypesModel());
        requestStatus = new JTextField("Status...");
        requestStatus.setEditable(false);
        requestMessage = new JTextField("");

        setLayout(new BorderLayout());

        add(new JLabel("Keyrequest to user " + connection.getConnectedUserName()), BorderLayout.NORTH);

        JPanel controls = new JPanel();
        controls.setLayout(new GridLayout(4,2));
        JLabel typeLabel = new JLabel("Encryption type:");
        typeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        controls.add(typeLabel);
        controls.add(encryptionTypeChooser);
        JLabel statusLabel = new JLabel("Request status:");
        statusLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        controls.add(statusLabel);
        controls.add(requestStatus);
        JLabel messageLabel = new JLabel("Request message:");
        messageLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        controls.add(messageLabel);
        controls.add(requestMessage);
        controls.add(closeWindowButton);
        controls.add(sendRequestButton);

        add(controls, BorderLayout.SOUTH);

        pack();
        setVisible(true);
    }

    public String getRequestType() {
        return requestType;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == closeWindowButton){
            this.dispose();
        }
        if(e.getSource() == sendRequestButton){
            final String chosenType = encryptionTypeChooser.getSelectedItem().toString();
            requestType = encryptionTypeChooser.getSelectedItem().toString();
            connection.sendKeyRequest(requestMessage.getText(),requestType);
            sendRequestButton.setEnabled(false);
            Runnable timer = new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println("Starting timer...");
                        requestStatus.setText("Waiting for key...");
                        TimeUnit.SECONDS.sleep(60);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(!connection.hasConnectedUserKey(chosenType)){
                        requestStatus.setText("Request timeout!");
                    }
                }
            };
            new Thread(timer).start();
        }
    }
}
