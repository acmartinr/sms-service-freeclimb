package services.sms.model.v3;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Errors {

    @JsonProperty("Error")
    private List<SMSApiError> error;

    public Errors() {}

    public List<SMSApiError> getError() {
        return error;
    }

    public void setError(List<SMSApiError> error) {
        this.error = error;
    }

}
