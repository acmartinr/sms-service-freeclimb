package model;

public class AddFundRequest {

    private float value;
    private long userId;

    public AddFundRequest() {}

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

}
