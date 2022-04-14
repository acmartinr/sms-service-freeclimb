package services.sms.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FreeClimbPhoneNumber {

    @JsonProperty("phoneNumber")
    String phoneNumber;
    @JsonProperty("phoneNumberId")
    String phoneNumberId;
    @JsonProperty("alias")
    String alias;
    @JsonProperty("region")
    String region;
    @JsonProperty("country")
    String country;
    @JsonProperty("voiceEnabled")
    String voiceEnabled;
    @JsonProperty("smsEnabled")
    String smsEnabled;

    public FreeClimbPhoneNumber() {
        this.phoneNumber = "";
        this.phoneNumberId = "";
        this.alias = "";
        this.region = "";
        this.country = "";
        this.voiceEnabled = "";
        this.smsEnabled = "";
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumberId() {
        return phoneNumberId;
    }

    public void setPhoneNumberId(String phoneNumberId) {
        this.phoneNumberId = phoneNumberId;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getVoiceEnabled() {
        return voiceEnabled;
    }

    public void setVoiceEnabled(String voiceEnabled) {
        this.voiceEnabled = voiceEnabled;
    }

    public String getSmsEnabled() {
        return smsEnabled;
    }

    public void setSmsEnabled(String smsEnabled) {
        this.smsEnabled = smsEnabled;
    }
}
