package com.tm;

// notifies when user goes offline/online
public interface UserStatusListener {
    public void online(String login);
    public void offline(String login);
}
