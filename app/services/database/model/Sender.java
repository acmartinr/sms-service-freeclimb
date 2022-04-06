package services.database.model;

public class Sender {

    private long id;
    private long userId;
    private String name;
    private long phone;
    private long sentCount;
    private long date;

    public Sender() {}

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
