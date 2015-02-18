import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

/**
 * Created by hugiasgeirsson on 17/02/15.
 */
public class ChatFileTransfer {
    private ChatFileTransferSendWindow sendWindow;
    private ChatFileTransferAcceptWindow acceptWindow;
    private Socket socket;
    private ServerSocket serverSocket;
    private File targetFile;
    private ChatConnection connection;
    private ChatMessage message;
    private boolean activeTransfer;
    private boolean sendMode;

    public ChatFileTransfer() {
        this.sendMode = true;
        startTimeOutThread();
    }

    public ChatFileTransfer(ChatMessage message, ChatConnection connection){
        this.connection = connection;
        this.message = message;
        this.sendMode = false;
        this.acceptWindow = new ChatFileTransferAcceptWindow(connection, message, this);
        startTimeOutThread();
    }

    public void openSendFileConnection(int port) {
        try {
            System.out.println("Prepare file send...");
            serverSocket = new ServerSocket(port);
            System.out.println("Socket accepted");
            socket = serverSocket.accept();
            activeTransfer = true;
            System.out.println(" Server established connection: " + socket);
        } catch (IOException e) {
            System.out.println("Could not establish connection. " + e);
        }
    }

    Runnable serverTimeoutThread = new Runnable() {
        @Override
        public void run() {
            try {
                System.out.println("Starting timer...");
                TimeUnit.SECONDS.sleep(60);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(!activeTransfer){
                System.out.println("Request timeout!");
                disconnect();
                sendWindow.dispose();
                new ChatErrorPromptWindow("File transfer request timeout!");
            }
        }
    };

    Runnable clientTimeoutThread = new Runnable() {
        @Override
        public void run() {
            try {
                System.out.println("Starting timer...");
                TimeUnit.SECONDS.sleep(60);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(!activeTransfer){
                System.out.println("Request timeout!");
                disconnect();
                acceptWindow.dispose();
                new ChatErrorPromptWindow("File transfer request timeout!");
            }
        }
    };

    Runnable timeOutThread = new Runnable() {
        @Override
        public void run() {
            try {
                System.out.println("Starting timer...");
                TimeUnit.SECONDS.sleep(60);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(!activeTransfer){
                System.out.println("Request timeout!");
                disconnect();
                if(sendMode){
                    sendWindow.dispose();
                } else {
                    acceptWindow.dispose();
                }
                new ChatErrorPromptWindow("File transfer request timeout!");
            }
        }
    };

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

    public boolean setFileToSave(){
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
            return true;
        } else {
            return false;
        }
    }

    Runnable acceptFileThread = new Runnable() {
        @Override
        public void run() {
            acceptFile((int) message.getFileSize(), targetFile, acceptWindow.getReplyMessageString());
        }
    };

    private void acceptFile(int fileSize, File targetFile, String responseMessage) {
        try {
            activeTransfer = true;
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
                        if(tries==10){
                            acceptWindow.setRequestMessageLabel("SERVER TIMEOUT!");
                            disconnect();
                        }
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

            acceptWindow.initiateProgressBar(fileSize);

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
                acceptWindow.updateProgressBar(totalBytesRead);
            } while (bytesRead>0);

            bufferedOutputStream.flush();
            bufferedOutputStream.close();
            socket.close();
            acceptWindow.dispose();

        } catch (FileNotFoundException e) {
            System.out.println("File not found! " + e);
            disconnect();
            acceptWindow.dispose();
        } catch (IOException e) {
            System.out.println("Could not establish connection to accept. " + e);
            disconnect();
            acceptWindow.dispose();
        }
    }

    public void disconnect(){
        try {
            socket.close();
            activeTransfer = false;
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (NullPointerException e2){
            System.out.println("No socket open!");
        }
        System.out.println("Socket closed");
    }

    public void startAcceptFileThread(){
        new Thread(acceptFileThread).start();
    }

    public void startServerTimeoutThread(){
        new Thread(serverTimeoutThread).start();
    }

    public void startClientTimeoutThread(){
        new Thread(clientTimeoutThread).start();
    }

    public void startTimeOutThread(){
        new Thread(timeOutThread).start();
    }

    public ChatFileTransferSendWindow getSendWindow() {
        return sendWindow;
    }

    public void setSendWindow(ChatFileTransferSendWindow sendWindow) {
        this.sendWindow = sendWindow;
    }

}
