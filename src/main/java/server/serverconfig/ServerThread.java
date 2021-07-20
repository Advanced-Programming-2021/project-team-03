package server.serverconfig;

import server.control.MainController;

public class ServerThread extends Thread {
    public String run(String message) {
        return MainController.getInstance().getRequest(message);
    }
}