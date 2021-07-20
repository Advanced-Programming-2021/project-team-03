package server.serverconfig;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static Server instance;
    private static final String HOST = "127.0.0.1";
    private final static int PORT = 7777;

    private Server() {
    }

    public static Server getInstance() {
        if (instance == null) instance = new Server();
        return instance;
    }

    public void runServer() {
        try {
            ServerSocket serverSocket =
                    new ServerSocket(PORT, 1000, InetAddress.getByName(HOST));

            Socket socket;
            DataInputStream dataInputStream;
            DataOutputStream dataOutputStream;
            while (true) {
                try {
                    socket = serverSocket.accept();

                    dataOutputStream = new DataOutputStream(socket.getOutputStream());
                    dataInputStream = new DataInputStream(socket.getInputStream());
                    String input = dataInputStream.readUTF();

                    ServerThread serverThread = new ServerThread();
                    serverThread.start();
                    String message = serverThread.run(input);
                    dataOutputStream.writeUTF(message);
                    dataOutputStream.flush();
                    dataInputStream.close();
                    dataOutputStream.close();
                    socket.close();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
