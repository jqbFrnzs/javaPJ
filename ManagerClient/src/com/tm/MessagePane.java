package com.tm;

import javax.swing.*;

public class MessagePane extends JPanel {

    private final ManagerClient client;
    private final String login;


    public MessagePane(ManagerClient client, String login) {
        this.client = client;
        this.login = login;
    }
}
