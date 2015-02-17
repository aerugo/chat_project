import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

/**
 * Created by hugiasgeirsson on 10/02/15.
 */
public class FileTransferConnection {
    private ChatFileTransferSendWindow sendWindow;
    private ChatFileTransferAcceptWindow acceptWindow;
    private ChatConnection mainConnection;
    private Socket socket;

    public FileTransferConnection(ChatConnection connection) {
        this.mainConnection = connection;
    }

    public void prepareFileSend(int port) {
        try {
            System.out.println("Prepare file send...");
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Socket accepted");
            socket = serverSocket.accept();
            System.out.println("Server established connection: " + socket);
        } catch (IOException e) {
            System.out.println("Could not establish connection. " + e);
        }
    }

    public void sendFile(File file) {
        try {
            byte[] byteArray = new byte[(int) file.length()];
            FileInputStream fileInputStream = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            bufferedInputStream.read(byteArray, 0, byteArray.length);
            System.out.println("File send...");
            OutputStream outputStream = socket.getOutputStream();
            System.out.println("Output stream open...");
            outputStream.write(byteArray,0, byteArray.length);
            System.out.println("Write ok...");
            outputStream.flush();
            socket.close();
        } catch (NullPointerException e) {
            System.out.println("Array not initialized! " + e);
        } catch (IOException e) {
            System.out.println("Could not establish connection to send. " + e);
        }
    }

    public void acceptFile(int fileSize, File targetFile, String responseMessage) {
        try {
            ChatMessage fileResponse = new ChatMessage(responseMessage,"yes");
            fileResponse.setFileRequestPort(9822);
            fileResponse.setMessageType("fileresponse");
            mainConnection.sendMessage(fileResponse);
            System.out.println("Accept file...");
            System.out.println(mainConnection.getClientSocket().getInetAddress());
            Socket socket = new Socket(mainConnection.getClientSocket().getInetAddress(), 9822);
            System.out.println("Socket open to " + socket);
            byte[] bytearray = new byte[fileSize+512];
            InputStream inputStream = socket.getInputStream();
            System.out.println("Input stream open on " + socket);
            FileOutputStream fileOutputStream = new FileOutputStream(targetFile);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            // Debug print
            System.out.println("Message sent" + mainConnection.getSession().encoderDecoder.chatMessageToXML(fileResponse));

            acceptWindow.initiateProgressBar(fileSize);
            int bytesRead = 0;
            int tries = 0;
            while (tries<10){
                bytesRead = inputStream.read(bytearray,0,bytearray.length);
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
            int totalBytesRead = bytesRead;

            do {
                acceptWindow.updateProgressBar(totalBytesRead);
                bytesRead = inputStream.read(bytearray,totalBytesRead,bytearray.length-totalBytesRead);
                if(bytesRead>=0){
                    totalBytesRead += bytesRead;
                }
                System.out.println(totalBytesRead);
            } while (bytesRead>0);

            bufferedOutputStream.write(bytearray, 0, totalBytesRead);
            bufferedOutputStream.flush();
            bufferedOutputStream.close();
            socket.close();

        } catch (FileNotFoundException e) {
            System.out.println("File not found! " + e);
        } catch (IOException e) {
            System.out.println("Could not establish connection to accept. " + e);
        }
    }

    public void setSendWindow(ChatFileTransferSendWindow sendWindow) {
        this.sendWindow = sendWindow;
    }

    public ChatFileTransferSendWindow getSendWindow() {
        return sendWindow;
    }

    public ChatFileTransferAcceptWindow getAcceptWindow() {
        return acceptWindow;
    }

    public void setAcceptWindow(ChatFileTransferAcceptWindow acceptWindow) {
        this.acceptWindow = acceptWindow;
    }
}