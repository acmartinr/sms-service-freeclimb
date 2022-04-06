package services.database.model;

public class CampaignError {

    private long id;
    private long campaignId;
    private long phone;
    private String error;
    private long date;

    public CampaignError() {}

    public CampaignError(long campaignId, long phone, String error, long date) {
        this.campaignId = campaignId;
        this.phone = phone;
        this.error = error;
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(long campaignId) {
        this.campaignId = campaignId;
    }

    public long getPhone() {
        return phone;
    }

    public void setPhone(long phone) {
        this.phone = phone;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

}
