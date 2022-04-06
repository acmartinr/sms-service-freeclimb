package model;

import services.database.model.SenderGroup;

import java.util.List;

public class SenderGroupsResponse {

    private List<SenderGroup> groups;
    private int count;

    public SenderGroupsResponse() {}

    public SenderGroupsResponse(List<SenderGroup> groups,
                                int count) {
        this.groups = groups;
        this.count = count;
    }

    public List<SenderGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<SenderGroup> groups) {
        this.groups = groups;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
