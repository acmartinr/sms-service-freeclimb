package model;

import services.database.model.Phone;

import java.util.List;

public class PhonesResponse {

    private List<Phone> phones;
    private int count;

    public PhonesResponse() {}

    public PhonesResponse(List<Phone> phones,
                          int count) {
        this.phones = phones;
        this.count = count;
    }

    public List<Phone> getPhones() {
        return phones;
    }

    public void setPhones(List<Phone> phones) {
        this.phones = phones;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
