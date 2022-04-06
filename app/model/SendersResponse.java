package model;

import services.database.model.Sender;

import java.util.List;

public class SendersResponse {

    private List<Sender> senders;
    private int count;

    public SendersResponse() {}

    public SendersResponse(List<Sender> senders,
                           int count) {
        this.senders = senders;
        this.count = count;
    }

    public List<Sender> getSenders() {
        return senders;
    }

    public void setSenders(List<Sender> senders) {
        this.senders = senders;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
