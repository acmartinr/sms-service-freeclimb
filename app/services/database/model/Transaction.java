package services.database.model;

public class Transaction {

    public static final int ADD_FUND_MANUALLY = 0;

    public static final int PHONE_BUY = 1;
    public static final int PHONE_RENEW = 4;

    public static final int INBOUND_MESSAGE = 2;
    public static final int OUTBOUND_MESSAGE = 3;
    public static final int CARRIER_SURCHARGE = 5;
    public static final int CARRIER_LOOKUP = 6;


    public static final int SUB_USER_PHONE_BUY = 11;
    public static final int SUB_USER_PHONE_RENEW = 14;

    public static final int SUB_USER_INBOUND_MESSAGE = 12;
    public static final int SUB_USER_OUTBOUND_MESSAGE = 13;
    public static final int SUB_USER_CARRIER_SURCHARGE = 15;
    public static final int SUB_USER_CARRIER_LOOKUP = 16;

    private long id;
    private long userId;
    private String username;
    private int type;
    private float amount;
    private long date;
    private String details;

    public Transaction() {}

    public Transaction(long userId, float amount, int type, long date, String details) {
        this.userId = userId;
        this.amount = amount;
        this.type = type;
        this.date = date;
        this.details = details;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
