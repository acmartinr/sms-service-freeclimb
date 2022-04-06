package services.database.model;

import java.util.LinkedList;
import java.util.List;

public class SenderGroup {

    private long id;
    private long userId;
    private String name;
    private long date;
    private int sendersCount;

    private List<Sender> senders = new LinkedList();

    public SenderGroup() {}

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

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getSendersCount() {
        return sendersCount;
    }

    public void setSendersCount(int sendersCount) {
        this.sendersCount = sendersCount;
    }

    public List<Sender> getSenders() {
        return senders;
    }

    public void setSenders(List<Sender> senders) {
        this.senders = senders;
    }
}
