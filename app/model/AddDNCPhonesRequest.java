package model;

import java.util.List;

public class AddDNCPhonesRequest {

    private long userId;
    private long listId;
    private List<Long> phones;

    public AddDNCPhonesRequest() {}

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public List<Long> getPhones() {
        return phones;
    }

    public void setPhones(List<Long> phones) {
        this.phones = phones;
    }

    public long getListId() {
        return listId;
    }

    public void setListId(int listId) {
        this.listId = listId;
    }
}
