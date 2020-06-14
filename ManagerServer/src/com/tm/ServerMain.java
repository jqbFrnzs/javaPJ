package com.tm;

public class ServerMain {
    public static void main(String[] args) {
        int port = 8819;
        Server server = new Server(port);
        server.start();
    }
}