package services.database.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Setting {

    private int id;
    private String skey;
    private String sval;
    private long userId;

    public Setting() {}

    public Setting(String skey, String sval) {
        this.skey = skey;
        this.sval = sval;
    }

    public Setting(String skey, String sval, Long userId) {
        this.skey = skey;
        this.sval = sval;
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSkey() {
        return skey;
    }

    public void setSkey(String skey) {
        this.skey = skey;
    }

    public String getSval() {
        return sval;
    }

    public void setSval(String sval) {
        this.sval = sval;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    @JsonIgnore
    public Long getSvalAsLong() {
        try {
            return Long.parseLong(sval);
        } catch (Exception e) { e.printStackTrace(); }

        return null;
    }
}
