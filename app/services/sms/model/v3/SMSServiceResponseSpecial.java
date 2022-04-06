package services.sms.model.v3;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SMSServiceResponseSpecial {

    @JsonProperty("Message360")
    private Message360Special message360;

    public SMSServiceResponseSpecial() {}

    public Message360Special getMessage360() {
        return message360;
    }

    public void setMessage360(Message360Special message360) {
        this.message360 = message360;
    }
}
