package model;

import services.database.model.User;

import java.util.List;

public class UsersListsResponse {

    private List<User> users;
    private int count;

    public UsersListsResponse() {}

    public UsersListsResponse(List<User> users,
                              int count) {
        this.users = users;
        this.count = count;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
