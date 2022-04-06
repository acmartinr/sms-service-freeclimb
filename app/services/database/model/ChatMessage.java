package services.database.model;

public class ChatMessage {

    private long id;
    private long chatId;
    private boolean inbound;
    private long date;
    private String message;
    private boolean read;
    private String externalId;
    private long phoneFrom;
    private boolean manual;

    public ChatMessage() {}

    public ChatMessage(long chatId, String message, boolean inbound,
                       long date, boolean read, String externalId,
                       long phoneFrom, boolean manual) {
        this.chatId = chatId;
        this.message = message;
        this.inbound = inbound;
        this.date = date;
        this.read = read;
        this.externalId = externalId;
        this.phoneFrom = phoneFrom;
        this.manual = manual;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public boolean isInbound() {
        return inbound;
    }

    public void setInbound(boolean inbound) {
        this.inbound = inbound;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public long getPhoneFrom() {
        return phoneFrom;
    }

    public void setPhoneFrom(long phoneFrom) {
        this.phoneFrom = phoneFrom;
    }

    public boolean isManual() {
        return manual;
    }

    public void setManual(boolean manual) {
        this.manual = manual;
    }
}
