package server.serverconfig;

import server.control.MainController;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class Request implements Runnable {
    private final String input;
    private String message;
    private final DataInputStream dataInputStream;
    private final DataOutputStream dataOutputStream;
    private final Socket socket;

    public Request(String input, DataInputStream dataInputStream, DataOutputStream dataOutputStream, Socket socket) {
        this.input = input;
        this.dataInputStream = dataInputStream;
        this.dataOutputStream = dataOutputStream;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            message = MainController.getInstance().getRequest(input);
            dataOutputStream.writeUTF(message);
            dataOutputStream.flush();
            dataInputStream.close();
            dataOutputStream.close();
            socket.close();
        } catch (Exception ignored) {
        }
    }

    public String getMessage() {
        return message;
    }
}
