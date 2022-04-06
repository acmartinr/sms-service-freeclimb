package services.sms.model.v3;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import services.sms.model.SMSApiMessage;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Message360Special {

    @JsonProperty("Errors")
    private List<SMSApiError> errors;

    @JsonProperty("Phones")
    private Phones phones;

    @JsonProperty("Messages")
    private Messages messages;

    @JsonProperty("Message")
    private SMSApiMessage message;

    public Message360Special() {}

    public List<SMSApiError> getErrors() {
        return errors;
    }

    public void setErrors(List<SMSApiError> errors) {
        this.errors = errors;
    }

    public Phones getPhones() {
        return phones;
    }

    public void setPhones(Phones phones) {
        this.phones = phones;
    }

    public Messages getMessages() {
        return messages;
    }

    public void setMessages(Messages messages) {
        this.messages = messages;
    }

    public SMSApiMessage getMessage() {
        return message;
    }

    public void setMessage(SMSApiMessage message) {
        this.message = message;
    }

}
