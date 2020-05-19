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
                Thread t = new Thread() {
                    @Override
                    public void run() {
                        try {
                            handleClientSocket(clientSocket);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClientSocket(Socket clientSocket) throws IOException, InterruptedException {
        OutputStream outputStream = clientSocket.getOutputStream();
        for (int i = 0; i < 10; i++) {
            outputStream.write(("Time: " + new Date() + "\n").getBytes());
            Thread.sleep(1000);
        }
        clientSocket.close();
    }
}