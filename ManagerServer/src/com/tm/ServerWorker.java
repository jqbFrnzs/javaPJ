package com.tm;

import ch.qos.logback.classic.Logger;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.HashSet;
import java.util.List;



public class ServerWorker extends Thread{

    private final Socket clientSocket;
    private final Server server;
    private String login = null;
    private OutputStream outputStream;
    private HashSet<String> topicSet = new HashSet<>();

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
//    @Override
//    public void run() {
//        try {
//            handleClientSocket();
//        } catch (IOException | InterruptedException e) {
//            if (e.getMessage().equalsIgnoreCase("Connection reset")) {
//                System.out.println("Client disconnected, waiting for another connection . . .");
//            } else {
//                e.printStackTrace();
//            }
//        }
//    }
    private void handleClientSocket() throws IOException, InterruptedException {
        InputStream inputStream = clientSocket.getInputStream();
        this.outputStream = clientSocket.getOutputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ( (line = reader.readLine()) != null) {
            String[] tokens = StringUtils.split(line);
            if (tokens != null && tokens.length > 0) {
                String cmd = tokens[0];
                if ("logoff".equals(cmd) || "quit".equalsIgnoreCase(cmd)) {
                    handleLogoff();
                    break;
                } else if ("login".equalsIgnoreCase(cmd)) {
                    handleLogin(outputStream, tokens);
                } else if ("msg".equalsIgnoreCase(cmd)) {
                    // split ONLY 3 tokens so you can send space-separated messages (user-user)
                    String[] tokensMsg = StringUtils.split(line, null, 3);
                    handleMessage(tokensMsg);
                } else if ("join".equalsIgnoreCase(cmd)) {
                    handleJoin(tokens);
                } else if ("leave".equalsIgnoreCase(cmd)) {
                    handleLeave(tokens);
                } else {
                    String msg = "unknown " + cmd + "\n";
                    outputStream.write(msg.getBytes());
                }
            }
        }
        clientSocket.close();
    }
    // function handles user's topic leaving
    private void handleLeave(String[] tokens) {
        if (tokens.length > 1) {
            String topic = tokens[1];
            topicSet.remove(topic);
        }
    }

    public boolean isMemberOfTopic(String topic) {
        // checks if user is member of topic
        return topicSet.contains(topic);
    }

    private void handleJoin(String[] tokens) {
        // adds topic to a user
        if (tokens.length > 1) {
            String topic = tokens[1];
            topicSet.add(topic);
        }
    }

    // format: "msg" "login" body . . .
    // format1: "msg" "#topic" body . . .
    private void handleMessage(String[] tokens) throws IOException {
        String sendTo = tokens[1];
        String body = tokens[2];
        // checks if first char is '#'
        boolean isTopic = sendTo.charAt(0) == '#';

        List<ServerWorker> workerList = server.getWorkerList();
        for (ServerWorker worker : workerList) {
            // sends broadcast message if user is member of certain #topic
            if (isTopic) {
                if(worker.isMemberOfTopic(sendTo)) {
                    String outMsg = "msg " + sendTo + ":" + login + " " + body + "\n";
                    worker.send(outMsg);
                }
            } else {
                if (sendTo.equalsIgnoreCase(worker.getLogin())) {
                    String outMsg = "msg " + login + " " + body + "\n";
                    worker.send(outMsg);
                }
            }
        }
    }

    private void handleLogoff() throws IOException {
        server.removeWorker(this);
        List<ServerWorker> workerList = server.getWorkerList();

        // send other online users current user's status
        String onlineMsg = "offline " + login + "\n";
        for(ServerWorker worker : workerList) {
            // do not send ONLINE message to oneself
            if (!login.equals(worker.getLogin())) {
                worker.send(onlineMsg);
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

            if((login.equals("guest") && password.equals("guest")) || ((login.equals("jqb") && password.equals("jqb")))
                    || ((login.equals("adam") && password.equals("adam"))) ||
                    ((login.equals("tomek") && password.equals("tomek")))) {
                String msg = "ok login\n";
                outputStream.write(msg.getBytes());
                this.login = login;
                System.out.println("User logged in succesfully: " + login);

                List<ServerWorker> workerList = server.getWorkerList();
                // send current user all other online logins
                for(ServerWorker worker : workerList) {
                    // do not send ONLINE message when clientSocket is not logged in
                    if (worker.getLogin() != null) {
                        if (!login.equals(worker.getLogin())) {
                            String msg2 = "online " + worker.getLogin() + "\n";
                            send(msg2);
                        }
                    }
                }

                String onlineMsg = "online " + login + "\n";
                // send other online users current user's status
                for(ServerWorker worker : workerList) {
                    // do not send ONLINE message to oneself
                    if (!login.equals(worker.getLogin())) {
                        worker.send(onlineMsg);
                    }
                }
            } else {
                String msg = "error login\n";
                outputStream.write(msg.getBytes());
                System.err.println("Login failed for " + login);
            }
        }
    }

    private void send(String msg) throws IOException {
        if (login != null) {
            // do not send ONLINE message if clientSocket is opened but user not connected
            try {
                outputStream.write(msg.getBytes());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
