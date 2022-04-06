package services.sms.model.v3;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import services.sms.model.SMSApiPhone;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Phones {

    @JsonProperty("Phone")
    private List<SMSApiPhone> phone;

    public Phones() {}

    public List<SMSApiPhone> getPhone() {
        return phone;
    }

    public void setPhone(List<SMSApiPhone> phone) {
        this.phone = phone;
    }

}
