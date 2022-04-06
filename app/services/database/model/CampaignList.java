package services.database.model;

public class CampaignList {

    private long id;
    private String name;
    private long userId;
    private long date;
    private long cnt;
    private long sentCount;
    private long receivedCount;
    private long ignored;
    private long dnc;
    private long errors;

    public CampaignList() {}

    public CampaignList(long userId, String name, long date, long cnt, long sentCount) {
        this.userId = userId;
        this.date = date;
        this.name = name;
        this.cnt = cnt;
        this.sentCount = sentCount;
    }

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

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getCnt() {
        return cnt;
    }

    public void setCnt(long cnt) {
        this.cnt = cnt;
    }

    public long getSentCount() {
        return sentCount;
    }

    public void setSentCount(long sentCount) {
        this.sentCount = sentCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getIgnored() {
        return ignored;
    }

    public void setIgnored(long ignored) {
        this.ignored = ignored;
    }

    public long getDnc() {
        return dnc;
    }

    public void setDnc(long dnc) {
        this.dnc = dnc;
    }

    public long getErrors() {
        return errors;
    }

    public void setErrors(long errors) {
        this.errors = errors;
    }

    public long getReceivedCount() {
        return receivedCount;
    }

    public void setReceivedCount(long receivedCount) {
        this.receivedCount = receivedCount;
    }
}
