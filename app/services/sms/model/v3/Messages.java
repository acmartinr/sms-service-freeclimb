package services.sms.model.v3;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import services.sms.model.SMSApiMessage;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Messages {

    @JsonProperty("Message")
    private List<SMSApiMessage> message;

    public Messages() {}

    public List<SMSApiMessage> getMessage() {
        return message;
    }

    public void setMessage(List<SMSApiMessage> message) {
        this.message = message;
    }

}
