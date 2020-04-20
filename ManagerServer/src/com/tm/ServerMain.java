package com.tm;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {
    public static void main(String[] args) throws IOException {
     int port = 8818;
        ServerSocket serverSocket = new ServerSocket(port);
        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                OutputStream outputStream = clientSocket.getOutputStream();
                outputStream.write("Hello my Client\n".getBytes());
                clientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}