package com.tm;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ManagerClient {
    private final String serverName;
    private final int serverPort;
    private Socket socket;
    private OutputStream serverOut;
    private InputStream serverIn;
    // used to get server response
    private BufferedReader bufferedIn;

    // register multiple user listeners to client (list of listeners)
    private ArrayList<UserStatusListener> userStatusListeners = new ArrayList<>();

    public ManagerClient(String serverName, int serverPort) {
        this.serverName = serverName;
        this.serverPort = serverPort;
    }

    public static void main(String[] args) throws IOException {
        ManagerClient client = new ManagerClient("localhost", 8818);
        client.addUserStatusListener(new UserStatusListener() {
            @Override
            public void online(String login) {
                System.out.println("ONLINE: " + login);
            }

            @Override
            public void offline(String login) {
                System.out.println("OFFLINE: " + login);
            }
        });
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
            startMessageReader();
            return true;
        } else {
            return false;
        }

    }

    // executes readMessageLoop after login
    private void startMessageReader() {
        Thread t = new Thread() {
            @Override
            public void run() {
                readMessageLoop();
            }
        };
        t.start();
    }

    // reads line by line from server output (our client input)
    private void readMessageLoop() {
        try {
            String line;
            while ((line = bufferedIn.readLine()) != null) {
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public void addUserStatusListener(UserStatusListener listener) {
        userStatusListeners.add(listener);
    }
    public void removeUserStatusListener(UserStatusListener listener) {
        userStatusListeners.remove(listener);
    }
}
