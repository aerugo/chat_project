import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Created by hugiasgeirsson on 15/02/15.
 */
public class ChatFileTransferAcceptWindow extends JFrame implements ActionListener{
    ChatConnection connection;
    int fileTransferPort;
    File targetFile;
    String requestMessage;
    JTextField replyMessageField;
    String fileName;
    long fileSize;
    JButton okButton;
    JButton rejectButton;
    JProgressBar progressBar;

    public ChatFileTransferAcceptWindow(ChatConnection connection, ChatMessage message){

        this.connection = connection;
        this.fileTransferPort = message.getFileRequestPort();
        this.requestMessage = message.getMessageString();
        this.fileName = message.getFileName();
        this.fileSize = message.getFileSize();

        setTitle("File transfer from " + connection.getConnectedUserName());
        setResizable(false);

        JLabel requestMessageLabel = new JLabel(requestMessage);
        okButton = new JButton("Ok");
        okButton.setSize(new Dimension(50, 20));
        okButton.addActionListener(this);
        rejectButton = new JButton("Reject");
        rejectButton.setSize(new Dimension(50, 20));
        rejectButton.addActionListener(this);
        replyMessageField = new JTextField("Reply message: File accepted");
        JPanel controlPanel = new JPanel();
        JLabel fileNameLabel = new JLabel(fileName);
        JLabel fileSizeLabel = new JLabel(String.valueOf(fileSize*0.001) + " kb");
        JPanel fileInfo = new JPanel();
        progressBar = new JProgressBar();

        controlPanel.setLayout(new GridLayout(1,2));
        controlPanel.add(rejectButton);
        controlPanel.add(okButton);

        fileInfo.setLayout(new GridLayout(2,2));
        fileInfo.add(new JLabel("File name:"));
        fileInfo.add(fileNameLabel);
        fileInfo.add(new JLabel("File size:"));
        fileInfo.add(fileSizeLabel);

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
            dispose();
        }

        if(e.getSource() == okButton){

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Output file");
            fileChooser.setSelectedFile(new File(fileName));
            int userSelection = fileChooser.showSaveDialog(this);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                targetFile = fileChooser.getSelectedFile();
                System.out.println("Save as: " + targetFile.getAbsolutePath());
            }
            FileTransferConnection ftc = new FileTransferConnection(connection);
            ftc.setAcceptWindow(this);
            ftc.acceptFile((int)fileSize, targetFile, replyMessageField.getText());
        }
    }

    public void initiateProgressBar(int maxValue){
        progressBar.setMaximum(maxValue);
        progressBar.setValue(0);
    }

    public void updateProgressBar(int newValue){
        progressBar.setValue(newValue);
    }
}
