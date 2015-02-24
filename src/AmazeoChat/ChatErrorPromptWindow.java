package AmazeoChat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by hugiasgeirsson on 15/02/15.
 */
public class ChatErrorPromptWindow extends JFrame implements ActionListener{
    JLabel errorMessage;
    JButton okButton;


    public ChatErrorPromptWindow(String errorMessageString){
        setTitle("Error message");
        setResizable(false);

        errorMessage = new JLabel(errorMessageString);
        okButton = new JButton("Ok");
        okButton.setSize(new Dimension(50, 20));
        okButton.addActionListener(this);
        setLayout(new GridLayout(2, 1));
        add(errorMessage);
        add(okButton);
        pack();
        setVisible(true);

    }

    public void closeWindow(){
        this.dispose();
    }

    public void disableClose(){
        okButton.setVisible(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        dispose();
    }
}
