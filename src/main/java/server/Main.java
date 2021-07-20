package server;

import server.serverconfig.Server;

public class Main {
    public static void main(String[] args) {
        Server.getInstance().runServer();
    }
}
