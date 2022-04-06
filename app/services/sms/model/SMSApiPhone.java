package services.sms.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SMSApiPhone {

    @JsonProperty("PhoneNumber")
    private String phoneNumber;

    @JsonProperty("Region")
    private String region;

    @JsonProperty("MonthlyCost")
    private String monthlyCost;

    public SMSApiPhone() {}

    public SMSApiPhone(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getMonthlyCost() {
        return monthlyCost;
    }

    public void setMonthlyCost(String monthlyCost) {
        this.monthlyCost = monthlyCost;
    }
}

