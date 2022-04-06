package model;

import services.database.model.CampaignList;
import services.database.model.SenderGroup;

import java.util.List;

public class CampaignListsResponse {

    private List<CampaignList> lists;
    private int count;

    public CampaignListsResponse() {}

    public CampaignListsResponse(List<CampaignList> lists,
                                 int count) {
        this.lists = lists;
        this.count = count;
    }

    public List<CampaignList> getLists() {
        return lists;
    }

    public void setLists(List<CampaignList> lists) {
        this.lists = lists;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
