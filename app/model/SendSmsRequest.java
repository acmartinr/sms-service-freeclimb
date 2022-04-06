package model;

public class SendSmsRequest {

    private int chatId;
    private String message;
    private long phoneFrom;

    public SendSmsRequest() {}

    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getPhoneFrom() {
        return phoneFrom;
    }

    public void setPhoneFrom(long phoneFrom) {
        this.phoneFrom = phoneFrom;
    }
}
