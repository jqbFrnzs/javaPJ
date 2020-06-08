package com.tm;

import java.io.*;
import java.net.Socket;

public class ManagerClient {
    private final String serverName;
    private final int serverPort;
    private Socket socket;
    private OutputStream serverOut;
    private InputStream serverIn;
    // used to get server response
    private BufferedReader bufferedIn;

    public ManagerClient(String serverName, int serverPort) {
        this.serverName = serverName;
        this.serverPort = serverPort;
    }

    public static void main(String[] args) throws IOException {
        ManagerClient client = new ManagerClient("localhost", 8818);
        if(!client.connect()) {
            System.err.println("Connection failed");
        } else {
            System.out.println("connection successful");
            if (client.login("guest", "guest")) {
                System.out.println("Login successful");
            } else {
                System.err.println("Login failed");
           }
        }
    }

    private boolean login(String login, String password) throws IOException {
        String cmd = "login " + login + " " + password + "\n";
        serverOut.write(cmd.getBytes());

        // prints out response of the server to the client
        String response = bufferedIn.readLine();
        System.out.println(response);

        // if server response is correct the function returns true
        if ("ok, logging you in...".equalsIgnoreCase(response)) {
            return true;
        } else {
            return false;
        }

    }

    private boolean connect() {
        try {
            this.socket = new Socket(serverName, serverPort);
            System.out.println("Client port is " + socket.getLocalPort());
            this.serverOut = socket.getOutputStream();
            this.serverIn = socket.getInputStream();
            this.bufferedIn = new BufferedReader(new InputStreamReader(serverIn));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
