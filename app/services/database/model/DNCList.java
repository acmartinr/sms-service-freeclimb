package services.database.model;

public class DNCList {

    public static final String MAIN = "DNC";
    public static final String MASTER = "Master DNC";

    private long id;
    private String name;
    private long userId;
    private long date;
    private long cnt;

    public DNCList() {}

    public DNCList(long userId, String name, long date, long cnt) {
        this.userId = userId;
        this.date = date;
        this.name = name;
        this.cnt = cnt;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
