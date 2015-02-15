import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by hugiasgeirsson on 15/02/15.
 */
public class ChatErrorPrompt extends JFrame implements ActionListener{
    JLabel errorMessage;
    JButton okButton;

    public ChatErrorPrompt(String errorMessageString){
        setTitle("Error message");

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

    @Override
    public void actionPerformed(ActionEvent e) {
        dispose();
    }
}
