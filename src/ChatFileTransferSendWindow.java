import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Created by hugiasgeirsson on 15/02/15.
 */
public class ChatFileTransferSendWindow extends JFrame implements ActionListener{
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

    public ChatFileTransferSendWindow(ChatConnection connection){
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

        setLayout(new GridLayout(5,2));
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
                File file = fileChooser.getSelectedFile();
                try {
                    fileName.setText(file.getCanonicalPath());
                    fileSize.setText(String.valueOf(file.length()*0.001) + " kb");
                }catch (Exception exception){
                    fileName.setText("File could not be selected");
                }
            }
        }
    }
}
