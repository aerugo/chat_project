import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by hugiasgeirsson on 15/02/15.
 */
public class ChatFileTransferAcceptWindow extends JFrame implements ActionListener{
    JLabel requestMessage;
    JButton okButton;
    JButton rejectButton;
    JTextField replyMessage;
    JPanel controlPanel;

    public ChatFileTransferAcceptWindow(String requestMessageString){
        setTitle("File transfer request");
        setResizable(false);

        requestMessage = new JLabel(requestMessageString);
        okButton = new JButton("Ok");
        okButton.setSize(new Dimension(50, 20));
        okButton.addActionListener(this);
        rejectButton = new JButton("Reject");
        rejectButton.setSize(new Dimension(50, 20));
        rejectButton.addActionListener(this);
        replyMessage = new JTextField("Reply message: File accepted");
        controlPanel = new JPanel();

        controlPanel.setLayout(new GridLayout(1,2));
        controlPanel.add(rejectButton);
        controlPanel.add(okButton);

        setLayout(new GridLayout(4, 1));
        add(new JLabel("File transfer request message:"));
        add(requestMessage);
        add(replyMessage);
        add(controlPanel);

        pack();
        setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if(e.getSource() == rejectButton){
            dispose();
        }
    }
}
