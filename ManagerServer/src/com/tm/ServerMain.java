package com.tm;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;


public class ServerMain {
    public static void main(String[] args) throws IOException {
     int port = 8818;
        ServerSocket serverSocket = new ServerSocket(port);
        try {
            while (true) {
                System.out.println("Going to accept client connection just now");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted connection from " + clientSocket);
                ServerWorker worker = new ServerWorker(clientSocket);
                worker.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}