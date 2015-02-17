import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by hugiasgeirsson on 17/02/15.
 */
public class ChatFileSend {
    private ChatFileTransferSendWindow sendWindow;
    private Socket socket;
    private ServerSocket serverSocket;

    public ChatFileSend() {}

    public void openFileConnection(int port) {
        try {
            System.out.println("Prepare file send...");
            serverSocket = new ServerSocket(port);
            System.out.println("Socket accepted");
            socket = serverSocket.accept();
            System.out.println("Server established connection: " + socket);
        } catch (IOException e) {
            System.out.println("Could not establish connection. " + e);
        }
    }

    public void sendFile(File file) {
        try {
            System.out.println("File send...");
            OutputStream outputStream = socket.getOutputStream();
            System.out.println("Output stream open...");

            int maxBufferSize = 512;
            byte[] byteArray = new byte[maxBufferSize];
            int totalBytesRead = 0;

            sendWindow.initiateProgressBar((int)file.length());

            FileInputStream fileInputStream = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            int bytesBuffered = bufferedInputStream.read(byteArray, 0, byteArray.length);

            totalBytesRead = bytesBuffered;

            System.out.println("Buffering started...");

            do{
                outputStream.write(byteArray, 0, bytesBuffered);
                //System.out.println(totalBytesRead + " bytes written...");
                int bytesAvailable = fileInputStream.available();
                int bufferSize = Math.min(maxBufferSize, bytesAvailable); //stackoverflow @sunil
                byteArray = new byte[bufferSize];
                bytesBuffered = bufferedInputStream.read(byteArray, 0, byteArray.length);
                totalBytesRead += bytesBuffered;
                sendWindow.updateProgressBar(totalBytesRead);
            } while (bytesBuffered>0);

            System.out.println("Write ok...");
            outputStream.flush();
            socket.close();
            serverSocket.close();
            System.out.println("Socket closed");
        } catch (NullPointerException e) {
            System.out.println("Array not initialized! " + e);
            disconnect();
        } catch (IOException e) {
            System.out.println("Could not establish connection to send. " + e);
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

    public ChatFileTransferSendWindow getSendWindow() {
        return sendWindow;
    }

    public void setSendWindow(ChatFileTransferSendWindow sendWindow) {
        this.sendWindow = sendWindow;
    }
}
