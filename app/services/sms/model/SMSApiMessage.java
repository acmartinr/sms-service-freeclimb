package services.sms.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SMSApiMessage {

    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    static {
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    @JsonProperty("Body")
    private String body;

    @JsonProperty("Date")
    private String dateSent;

    @JsonProperty("MessageSid")
    private String messageSid;

    @JsonProperty("CarrierSurcharge")
    private float carrierSurcharge;

    @JsonProperty("Carrier")
    private String carrier;

    public SMSApiMessage() {
    }

    public SMSApiMessage(String text, String date, String messageSid) {
        this.body = text;
        this.dateSent = date;
        this.messageSid = messageSid;
    }

    public SMSApiMessage(String text, long date, String messageSid) {
        this.body = text;
        this.dateSent = dateFormat.format(new Date(date));
        this.messageSid = messageSid;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getDateSent() {
        return dateSent;
    }

    public void setDateSent(String dateSent) {
        this.dateSent = dateSent;
    }

    public long getDate() {
        try {
            return Long.parseLong(dateSent);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                return dateFormat.parse(dateSent).getTime();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return 0l;
    }

    public String getMessageSid() {
        return messageSid;
    }

    public void setMessageSid(String messageSid) {
        this.messageSid = messageSid;
    }

    public float getCarrierSurcharge() {
        return carrierSurcharge;
    }

    public void setCarrierSurcharge(float carrierSurcharge) {
        this.carrierSurcharge = carrierSurcharge;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }
}
