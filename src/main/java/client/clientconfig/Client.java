package client.clientconfig;

import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {
    private static Client instance;
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 7777;

    private Client() {
    }

    public static Client getInstance() {
        if (instance == null) instance = new Client();
        return instance;
    }

    public String sendRequestToServer(String message) {
        Socket socket;
        try {
            socket = new Socket(HOST, PORT);
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream.writeUTF(message);
            dataOutputStream.flush();
            String answer = dataInputStream.readUTF();
            dataInputStream.close();
            dataOutputStream.close();
            socket.close();
            return answer;
        } catch (IOException e) {
            System.out.println("Can not send message to server!");
            return new JSONObject().put("Type", "Error")
                    .put("Value", "Can not send message to server!").toString();
        }
    }
}
