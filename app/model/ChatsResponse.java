package model;

import services.database.model.Chat;

import java.util.List;

public class ChatsResponse {

    private List<Chat> chats;
    private int count;

    public ChatsResponse() {}

    public ChatsResponse(List<Chat> chats,
                         int count) {
        this.chats = chats;
        this.count = count;
    }

    public List<Chat> getChats() {
        return chats;
    }

    public void setChats(List<Chat> chats) {
        this.chats = chats;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

}
