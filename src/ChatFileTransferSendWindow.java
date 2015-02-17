import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Created by hugiasgeirsson on 15/02/15.
 */
public class ChatFileTransferSendWindow extends JFrame implements ActionListener{
    ChatConnection mainConnection;
    JTextField requestMessage;
    JLabel requestMessageLabel;
    JButton sendButton;
    JButton cancelButton;
    JButton chooseFile;
    JLabel requestReplyLabel;
    JTextField requestReply;
    JTextField fileName;
    JLabel fileSizeLabel;
    JLabel fileSize;
    JFileChooser fileChooser;
    JProgressBar progressBar;
    File file;

    public ChatFileTransferSendWindow(ChatConnection connection){
        this.mainConnection = connection;
        setTitle("File transfer to " + connection.getConnectedUserName());
        setSize(new Dimension(400, 300));
        setResizable(false);

        requestMessage = new JTextField("I want to send you a file!");
        requestMessageLabel = new JLabel("Request message:");
        requestReply = new JTextField("Waiting for reply...");
        requestReply.setEditable(false);
        requestReplyLabel = new JLabel("Reply to request");
        sendButton = new JButton("Send");
        sendButton.setSize(new Dimension(50, 20));
        sendButton.addActionListener(this);
        sendButton.setEnabled(false);
        cancelButton = new JButton("Cancel");
        cancelButton.setSize(new Dimension(50, 20));
        cancelButton.addActionListener(this);
        chooseFile = new JButton("Choose file");
        chooseFile.addActionListener(this);
        fileName = new JTextField("File name");
        fileName.setEditable(false);
        fileSize = new JLabel("0");
        fileSizeLabel = new JLabel("File size");
        fileChooser = new JFileChooser();
        progressBar = new JProgressBar();

        setLayout(new GridLayout(6,2));
        add(chooseFile);
        add(fileName);
        add(fileSizeLabel);
        add(fileSize);
        add(requestMessageLabel);
        add(requestMessage);
        add(requestReplyLabel);
        add(requestReply);
        add(cancelButton);
        add(sendButton);
        add(new JLabel("Transfer progress:"));
        add(progressBar);

        pack();
        setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if(e.getSource() == cancelButton){
            dispose();
        }

        if(e.getSource() == chooseFile){
            int option = fileChooser.showOpenDialog(this);
            if(option == JFileChooser.APPROVE_OPTION) {
                file = fileChooser.getSelectedFile();
                try {
                    fileName.setText(file.getName());
                    fileSize.setText(String.valueOf(file.length()*0.001) + " kb");
                    sendButton.setEnabled(true);
                }catch (Exception exception){
                    fileName.setText("File could not be selected");
                }
            }
        }

        if(e.getSource() == sendButton){
            mainConnection.setFileToTransfer(file);
            ChatMessage fileSendMessage = new ChatMessage(fileName.getText(),
                    requestMessage.getText(),(int)file.length());
            mainConnection.sendMessage(fileSendMessage);
            FileTransferConnection transferConnection = new FileTransferConnection(mainConnection);
            transferConnection.setSendWindow(this);
            mainConnection.setActiveFileTransfer(transferConnection);
        }
    }

    public void setRequestReply(String requestReply) {
        this.requestReply.setText(requestReply);
    }

    public void initiateProgressBar(int maxValue){
        progressBar.setMaximum(maxValue);
        progressBar.setValue(0);
    }

    public void updateProgressBar(int newValue){
        progressBar.setValue(newValue);
    }
}
