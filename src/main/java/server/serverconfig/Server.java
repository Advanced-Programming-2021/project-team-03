package server.serverconfig;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static Server instance;
    private final static int PORT = 8585;
    private final ExecutorService threadPool;

    private Server() {
        threadPool = Executors.newFixedThreadPool(5);
    }

    public static Server getInstance() {
        if (instance == null) instance = new Server();
        return instance;
    }

    public void runServer() {
        try {
            ServerSocket serverSocket =
                    new ServerSocket(PORT);

            Socket socket;
            DataInputStream dataInputStream;
            DataOutputStream dataOutputStream;
            while (true) {
                try {
                    socket = serverSocket.accept();
                    dataOutputStream = new DataOutputStream(socket.getOutputStream());
                    dataInputStream = new DataInputStream(socket.getInputStream());
                    String input = dataInputStream.readUTF();
                    Runnable request = new Request(input, dataInputStream, dataOutputStream, socket);
                    threadPool.execute(request);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
