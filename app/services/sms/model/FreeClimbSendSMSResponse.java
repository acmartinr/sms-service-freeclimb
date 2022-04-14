package services.sms.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FreeClimbSendSMSResponse {
    @JsonProperty("uri")
    String uri;
    @JsonProperty("revision")
    String revision;
    @JsonProperty("dateCreated")
    String dateCreated;
    @JsonProperty("dateUpdated")
    String dateUpdated;
    @JsonProperty("messageId")
    String messageId;
    @JsonProperty("accountId")
    String accountId;
    @JsonProperty("from")
    String from;
    @JsonProperty("to")
    String to;
    @JsonProperty("text")
    String text;
    @JsonProperty("direction")
    String direction;
    @JsonProperty("notificationUrl")
    String notificationUrl;
    @JsonProperty("status")
    String status;
    @JsonProperty("applicationId")
    String applicationId;



    @JsonProperty("phoneNumberId")
    String phoneNumberId;

    public FreeClimbSendSMSResponse() {
        this.uri = "";
        this.revision = "";
        this.dateCreated = "";
        this.dateUpdated = "";
        this.messageId = "";
        this.accountId = "";
        this.from = "";
        this.to = "";
        this.text = "";
        this.direction = "";
        this.notificationUrl = "";
        this.status = "";
        this.applicationId = "";
        this.phoneNumberId = "";
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(String dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
    public String getPhoneNumberId() {
        return phoneNumberId;
    }

    public void setPhoneNumberId(String phoneNumberId) {
        this.phoneNumberId = phoneNumberId;
    }
    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getNotificationUrl() {
        return notificationUrl;
    }

    public void setNotificationUrl(String notificationUrl) {
        this.notificationUrl = notificationUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
