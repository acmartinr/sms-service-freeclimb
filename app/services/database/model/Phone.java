package services.database.model;

public class Phone {

    private long id;
    private long userId;
    private long phone;
    private long date;
    private long chargedDate;
    private long lastSentDate;
    private long sentCount;
    private long forwarding;
    private String note;
    private long inboundCount;
    private long daySentCount;
    private boolean tollFree;

    public Phone() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getPhone() {
        return phone;
    }

    public void setPhone(long phone) {
        this.phone = phone;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getSentCount() {
        return sentCount;
    }

    public void setSentCount(long sentCount) {
        this.sentCount = sentCount;
    }

    public long getForwarding() {
        return forwarding;
    }

    public void setForwarding(long forwarding) {
        this.forwarding = forwarding;
    }

    public long getChargedDate() {
        return chargedDate;
    }

    public void setChargedDate(long chargedDate) {
        this.chargedDate = chargedDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public long getInboundCount() {
        return inboundCount;
    }

    public void setInboundCount(long inboundCount) {
        this.inboundCount = inboundCount;
    }

    public long getDaySentCount() {
        return daySentCount;
    }

    public void setDaySentCount(long daySentCount) {
        this.daySentCount = daySentCount;
    }

    public long getLastSentDate() {
        return lastSentDate;
    }

    public void setLastSentDate(long lastSentDate) {
        this.lastSentDate = lastSentDate;
    }

    public boolean isTollFree() {
        return tollFree;
    }

    public void setTollFree(boolean tollFree) {
        this.tollFree = tollFree;
    }
}
