package services.sms.model.v3;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SMSServiceV3ApiResponse {

    @JsonProperty("Message360")
    private Message360 message360;

    public SMSServiceV3ApiResponse() {}

    public Message360 getMessage360() {
        return message360;
    }

    public void setMessage360(Message360 message360) {
        this.message360 = message360;
    }
}
