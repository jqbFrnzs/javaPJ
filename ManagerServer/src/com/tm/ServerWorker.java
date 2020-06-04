package com.tm;

import java.io.*;
import java.net.Socket;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class ServerWorker extends Thread{

    private final Socket clientSocket;
    private final Server server;
    private String login = null;
    private OutputStream outputStream;

    public ServerWorker(Server server, Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            handleClientSocket();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private void handleClientSocket() throws IOException, InterruptedException {
        InputStream inputStream = clientSocket.getInputStream();
        this.outputStream = clientSocket.getOutputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ( (line = reader.readLine()) != null) {
            String[] tokens = StringUtils.split(line);
            if (tokens !=null && tokens.length > 0) {
                String cmd = tokens[0];
                if ("quit".equalsIgnoreCase(cmd)) {
                    break;
                } else if ("login".equalsIgnoreCase(cmd)) {
                    handleLogin(outputStream, tokens);
                } else {
                    String msg = "unknown " + cmd + "\n";
                    outputStream.write(msg.getBytes());
                }
            }
        }
        clientSocket.close();
    }

    public String getLogin() {
        return login;
    }

    private void handleLogin(OutputStream outputStream, String[] tokens) throws IOException {
        if (tokens.length == 3) {
            String login = tokens[1];
            String password = tokens[2];

            if(login.equals("guest") && password.equals("guest") || (login.equals("jqb") && password.equals("jqb"))) {
                String msg = "ok, logging you in...\n";
                outputStream.write(msg.getBytes());
                this.login = login;
                System.out.println(login + " logged in just now!");


                List<ServerWorker> workerList = server.getWorkerList();
                // send current user all other online logins
                for(ServerWorker worker : workerList) {
                    // do not send ONLINE message when clientSocket is not logged in
                    if (worker.getLogin() != null) {
                        if (!login.equals(worker.getLogin())) {
                            String onlineBefore = worker.getLogin() + " is now online!\n";
                            send(onlineBefore);
                        }
                    }
                }

                String onlineMsg = login + " is now online!\n";
                // send other online users current user's status
                for(ServerWorker worker : workerList) {
                    // do not send ONLINE message to oneself
                    if (!login.equals(worker.getLogin())) {
                        worker.send(onlineMsg);
                    }
                }

            } else {
                String msg = "error, cannot logging you in...\n";
                outputStream.write(msg.getBytes());
            }
        }
    }

    private void send(String msg) throws IOException {
        if (login != null) {
            // do not send ONLINE message if clientSocket is opened but user not connected
            outputStream.write(msg.getBytes());
        }
    }
}
