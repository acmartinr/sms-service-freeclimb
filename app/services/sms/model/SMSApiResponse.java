package services.sms.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SMSApiResponse {

    @JsonProperty("status")
    private Boolean status;

    @JsonProperty("count")
    private Integer count;

    @JsonProperty("page")
    private Integer page;

    @JsonProperty("error")
    private List<SMSApiError> error;

    @JsonProperty("payload")
    private List<JsonNode> payload;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public List<SMSApiError> getError() {
        return error;
    }

    public void setError(List<SMSApiError> error) {
        this.error = error;
    }

    public List<JsonNode> getPayload() {
        return payload;
    }

    public void setPayload(List<JsonNode> payload) {
        this.payload = payload;
    }
}
