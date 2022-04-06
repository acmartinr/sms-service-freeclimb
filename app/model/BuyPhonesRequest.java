package model;

import java.util.LinkedList;
import java.util.List;

public class BuyPhonesRequest {

    private long userId;
    private List<String> phones;
    private long date;

    public BuyPhonesRequest() {}

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public List<String> getPhones() {
        return phones;
    }

    public void setPhones(List<String> phones) {
        this.phones = phones;
    }

    public List<Long> getFormattedPhones() {
        List<Long> result = new LinkedList();
        for (String phone: phones) {
            phone = phone.substring(1);

            result.add(Long.parseLong(phone));
        }

        return result;
    }

    public long getDate() {
        return System.currentTimeMillis();
    }
}
