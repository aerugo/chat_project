import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Created by hugiasgeirsson on 15/02/15.
 */
public class ChatFileTransferAcceptWindow extends JFrame implements ActionListener{
    private ChatFileTransfer fileTransfer;
    private JTextField requestMessageLabel;
    private JTextField replyMessageField;
    private JButton okButton;
    private JButton rejectButton;
    private JProgressBar progressBar;

    public ChatFileTransferAcceptWindow(ChatConnection connection, ChatMessage message, ChatFileTransfer fileAccept){

        this.fileTransfer = fileAccept;
        String requestMessage = message.getMessageString();
        String fileName = message.getFileName();
        long fileSize = message.getFileSize();

        setTitle("File transfer from " + connection.getConnectedUserName());
        setResizable(false);

        requestMessageLabel = new JTextField(requestMessage);
        okButton = new JButton("Ok");
        okButton.setSize(new Dimension(50, 20));
        okButton.addActionListener(this);
        rejectButton = new JButton("Reject");
        rejectButton.setSize(new Dimension(50, 20));
        rejectButton.addActionListener(this);
        replyMessageField = new JTextField("Reply message: File accepted");
        JPanel controlPanel = new JPanel();
        JLabel fileNameLabel = new JLabel(fileName);
        JLabel fileSizeLabel = new JLabel(String.valueOf(fileSize *0.001) + " kb");
        JPanel fileInfo = new JPanel();
        progressBar = new JProgressBar();

        controlPanel.setLayout(new GridLayout(1,2));
        controlPanel.add(rejectButton);
        controlPanel.add(okButton);

        fileInfo.setLayout(new GridLayout(3,2));
        fileInfo.add(new JLabel("File name:"));
        fileInfo.add(fileNameLabel);
        fileInfo.add(new JLabel("File size:"));
        fileInfo.add(fileSizeLabel);
        fileInfo.add(new JLabel("Transfer progress: "));
        fileInfo.add(progressBar);

        setLayout(new GridLayout(5, 1));
        add(new JLabel("File transfer request message:"));
        add(requestMessageLabel);
        add(replyMessageField);
        add(fileInfo);
        add(controlPanel);

        pack();
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if(e.getSource() == rejectButton){
            fileTransfer.disconnect();
            dispose();
        }

        if(e.getSource() == okButton){
            boolean fileSet = fileTransfer.setFileToSave();
            if(fileSet){
                okButton.setEnabled(false);
                fileTransfer.startAcceptFileThread();
            }
        }
    }

    public void initiateProgressBar(int maxValue){
        progressBar.setMaximum(maxValue);
        progressBar.setStringPainted(true);
        progressBar.setValue(0);
    }

    public void updateProgressBar(int newValue){
        progressBar.setValue(newValue);
    }

    public String getReplyMessageString() {return replyMessageField.getText();}

    public void setRequestMessageLabel(String text) {requestMessageLabel.setText(text);}
}
