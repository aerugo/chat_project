import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

/**
 * Created by hugiasgeirsson on 17/02/15.
 */
public class ChatFileAcceptDaemon extends Thread{
    File targetFile;
    ChatConnection connection;
    ChatMessage message;
    ChatFileTransferAcceptWindow window;
    Socket socket;

    public ChatFileAcceptDaemon(ChatMessage message, ChatConnection connection){
        this.connection = connection;
        this.message = message;
        window = new ChatFileTransferAcceptWindow(connection, message, this);
    }

    public void run(){
        System.out.println("Started accept fileSend");
        JFileChooser fileChooser = new JFileChooser();
        System.out.println("File chooser created");
        fileChooser.setDialogTitle("Output file");
        JFrame jf = new JFrame("Save dialog");      //Stackoverflow @vaxquis
        jf.setAlwaysOnTop(true);
        fileChooser.setSelectedFile(new File(message.getFileName()));
        System.out.println("File chooser selected file set");
        int userSelection = fileChooser.showSaveDialog(jf);
        jf.dispose();
        System.out.println("Save dialog shown");
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            targetFile = fileChooser.getSelectedFile();
            System.out.println("Save as: " + targetFile.getAbsolutePath());
        }

        acceptFile((int) message.getFileSize(), targetFile, window.getReplyMessageString());

        window.dispose();
        this.interrupt();
    }

    public void acceptFile(int fileSize, File targetFile, String responseMessage) {
        try {
            ChatMessage fileResponse = new ChatMessage(responseMessage,"yes");
            fileResponse.setFileRequestPort(9822);
            fileResponse.setMessageType("fileresponse");
            connection.sendMessage(fileResponse);
            System.out.println("Accept file...");
            System.out.println(connection.getClientSocket().getInetAddress());

            int tries = 0;
            while (tries<10){
                try{
                    this.socket = new Socket(connection.getClientSocket().getInetAddress(), 9822);
                    break;
                }
                catch (IOException e){
                    tries += 1;
                    try {
                        TimeUnit.SECONDS.sleep(1);
                        System.out.println("Socket has waited " + tries + " seconds...");
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            }

            System.out.println("Socket open to " + socket);

            int maxBufferSize = 512;
            byte[] byteArray = new byte[maxBufferSize];
            int totalBytesRead = 0;

            InputStream inputStream = socket.getInputStream();
            System.out.println("Input stream open on " + socket);
            FileOutputStream fileOutputStream = new FileOutputStream(targetFile);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);

            window.initiateProgressBar(fileSize);

            int bytesRead = 0;
            tries = 0;
            while (tries<10){
                bytesRead = inputStream.read(byteArray,0,byteArray.length);
                if(bytesRead != 0) {
                    break;
                }
                tries += 1;
                try {
                    TimeUnit.SECONDS.sleep(1);
                    System.out.println("Read has waited " + tries + " seconds...");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if(bytesRead == 0) {
                System.out.println("No bytes read, file empty or transfer went wrong.");
            }

            do {
                bufferedOutputStream.write(byteArray, 0, byteArray.length);
                //System.out.println("Bytes read total: " + totalBytesRead);
                byteArray = new byte[maxBufferSize];
                bytesRead = inputStream.read(byteArray,0,byteArray.length);
                //System.out.println("Bytes read now: " + bytesRead);
                totalBytesRead += bytesRead;
                window.updateProgressBar(totalBytesRead);
            } while (bytesRead>0);

            bufferedOutputStream.flush();
            bufferedOutputStream.close();
            socket.close();

        } catch (FileNotFoundException e) {
            System.out.println("File not found! " + e);
            disconnect();
        } catch (IOException e) {
            System.out.println("Could not establish connection to accept. " + e);
            disconnect();
        }
    }

    public void disconnect(){
        try {
            socket.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (NullPointerException e2){
            System.out.println("No socket open!");
        }
        System.out.println("Socket closed");
    }
}
