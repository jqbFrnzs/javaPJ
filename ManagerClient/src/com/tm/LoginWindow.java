package com.tm;

import javax.swing.*;

public class LoginWindow extends JFrame {
    JTextField loginField = new JTextField();
    JPasswordField passwordField = new JPasswordField();
    JButton loginButton = new JButton("Login");

    public LoginWindow() {
        super("Login");
    }
}
