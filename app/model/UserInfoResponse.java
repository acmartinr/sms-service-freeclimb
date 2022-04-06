package model;

import services.database.model.AdminMessage;

import java.util.List;

public class UserInfoResponse {

    private List<AdminMessage> messages;
    private float balance;

    public UserInfoResponse() {}

    public UserInfoResponse(List<AdminMessage> messages, float balance) {
        this.messages = messages;
        this.balance = balance;
    }

    public List<AdminMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<AdminMessage> messages) {
        this.messages = messages;
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

}
