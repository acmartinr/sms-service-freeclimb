package model;

import services.database.model.Campaign;
import services.database.model.Sender;

import java.util.List;

public class CampaignsResponse {

    private List<Campaign> campaigns;
    private int count;

    public CampaignsResponse() {}

    public CampaignsResponse(List<Campaign> campaigns,
                             int count) {
        this.campaigns = campaigns;
        this.count = count;
    }

    public List<Campaign> getCampaigns() {
        return campaigns;
    }

    public void setCampaigns(List<Campaign> campaigns) {
        this.campaigns = campaigns;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
