package model;

import java.util.Date;

public class YtelCommonResponse {

    private String messageSid;
    private String errorMessage;
    private float carrierSurcharge;
    private Date sentDate;

    public YtelCommonResponse() {
    }

    public Date getSentDate() {
        return sentDate;
    }

    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
    }

    public YtelCommonResponse(String messageSid, String errorMessage) {
        this.messageSid = messageSid;
        this.errorMessage = errorMessage;
    }

    public YtelCommonResponse(String messageSid, String errorMessage, Date sentDate) {
        this.messageSid = messageSid;
        this.errorMessage = errorMessage;
        this.sentDate = sentDate;
    }

    public String getMessageSid() {
        return messageSid;
    }

    public void setMessageSid(String messageSid) {
        this.messageSid = messageSid;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public float getCarrierSurcharge() {
        return carrierSurcharge;
    }

    public void setCarrierSurcharge(float carrierSurcharge) {
        this.carrierSurcharge = carrierSurcharge;
    }
}
