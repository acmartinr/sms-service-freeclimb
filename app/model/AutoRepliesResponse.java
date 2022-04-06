package model;

import services.database.model.AutoReply;
import services.database.model.User;

import java.util.List;

public class AutoRepliesResponse {

    private List<AutoReply> autoReplies;
    private int count;

    public AutoRepliesResponse() {}

    public AutoRepliesResponse(List<AutoReply> autoReplies,
                               int count) {
        this.autoReplies = autoReplies;
        this.count = count;
    }

    public List<AutoReply> getAutoReplies() {
        return autoReplies;
    }

    public void setAutoReplies(List<AutoReply> autoReplies) {
        this.autoReplies = autoReplies;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
