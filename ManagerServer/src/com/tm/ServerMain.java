package com.tm;

public class ServerMain {
    public static void main(String[] args) {
        Server server = new Server(Constants.port);
        server.start();
    }
}