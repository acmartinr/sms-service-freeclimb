package services.database.model;

import services.sms.model.SMSApiMessage;

public class Chat {

    private long id;
    private long userId;
    private Long campaignId;
    private long phoneFrom;
    private long phoneTo;
    private long lastDate;
    private String lastMessage;
    private String lastMessageSid;
    private boolean read;
    private boolean hasInbound;
    private String carrier;

    public Chat() {
    }

    public Chat(long userId, Long campaignId, long toPhone, long fromPhone) {
        this.userId = userId;
        this.campaignId = campaignId;
        this.phoneTo = toPhone;
        this.phoneFrom = fromPhone;
    }

    public SMSApiMessage getLastSmsApiMessage() {
        SMSApiMessage message = new SMSApiMessage();
        message.setMessageSid(this.getLastMessageSid());
        message.setDateSent(String.valueOf(this.getLastDate()));
        message.setBody(this.getLastMessage());
        message.setCarrier(this.getCarrier());
        return message;
    }

    public String getLastMessageSid() {
        return lastMessageSid;
    }

    public void setLastMessageSid(String lastMessageSid) {
        this.lastMessageSid = lastMessageSid;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getPhoneFrom() {
        return phoneFrom;
    }

    public void setPhoneFrom(long phoneFrom) {
        this.phoneFrom = phoneFrom;
    }

    public long getPhoneTo() {
        return phoneTo;
    }

    public void setPhoneTo(long phoneTo) {
        this.phoneTo = phoneTo;
    }

    public long getLastDate() {
        return lastDate;
    }

    public void setLastDate(long lastDate) {
        this.lastDate = lastDate;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }


    public boolean isHasInbound() {
        return hasInbound;
    }

    public void setHasInbound(boolean hasInbound) {
        this.hasInbound = hasInbound;
    }
}
