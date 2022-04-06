package model;

public class SendTestSMSRequest {

    private Long phoneFrom;
    private Long phoneTo;
    private Long campaignId;
    private String message;
    private String data;
    private String data2;

    public SendTestSMSRequest() {}

    public Long getPhoneFrom() {
        return phoneFrom;
    }

    public void setPhoneFrom(Long phoneFrom) {
        this.phoneFrom = phoneFrom;
    }

    public Long getPhoneTo() {
        return phoneTo;
    }

    public void setPhoneTo(Long phoneTo) {
        this.phoneTo = phoneTo;
    }

    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getData2() {
        return data2;
    }

    public void setData2(String data2) {
        this.data2 = data2;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
