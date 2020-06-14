package com.tm;

import javax.swing.*;
import java.awt.*;

public class UserListPane extends JPanel implements UserStatusListener {

    private final ManagerClient client;

    // show list of users
    private JList<String> userListUI;
    private DefaultListModel<String> userListModel;

    public UserListPane(ManagerClient client) {
        this.client = client;
        this.client.addUserStatusListener(this);

        userListModel = new DefaultListModel<>();
        userListUI = new JList<>();
        setLayout(new BorderLayout());
        add(new JScrollPane(userListUI), BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        ManagerClient client = new ManagerClient("localhost", 8819);

        UserListPane userListPane = new UserListPane(client);
        JFrame frame = new JFrame("User List");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 600);

        frame.getContentPane().add(userListPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    @Override
    public void online(String login) {
        userListModel.addElement(login);
    }

    @Override
    public void offline(String login) {
        userListModel.removeElement(login);
    }
}
