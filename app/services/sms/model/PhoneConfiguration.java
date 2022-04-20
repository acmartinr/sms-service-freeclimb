package services.sms.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PhoneConfiguration {

    @JsonProperty("friendlyName")
    private String friendlyName;

    @JsonProperty("hangupCallbackUrl")
    private String hangupCallbackUrl;

    @JsonProperty("hangupCallbackMethod")
    private String hangupCallbackMethod;

    @JsonProperty("heartbeatUrl")
    private String heartbeatUrl;

    @JsonProperty("heartbeatMethod")
    private String heartbeatMethod;

    @JsonProperty("voiceUrl")
    private String voiceUrl;

    @JsonProperty("voiceMethod")
    private String voiceMethod;

    @JsonProperty("voiceFallbackUrl")
    private String voiceFallbackUrl;

    @JsonProperty("voiceFallbackMethod")
    private String voiceFallbackMethod;

    @JsonProperty("smsUrl")
    private String smsUrl;

    @JsonProperty("smsMethod")
    private String smsMethod;

    @JsonProperty("smsFallbackUrl")
    private String smsFallbackUrl;

    @JsonProperty("smsFallbackMethod")
    private String smsFallbackMethod;

    public PhoneConfiguration() {
        this.friendlyName = "";
        this.voiceUrl = "http://dev.wsdevworld.com:9000/api/forward";
        this.voiceMethod = "POST";
        this.voiceFallbackUrl = "";
        this.voiceFallbackMethod = "";
        this.hangupCallbackUrl = "";
        this.hangupCallbackMethod = "";
        this.heartbeatUrl = "";
        this.heartbeatMethod = "";
        this.smsUrl = "http://dev.wsdevworld.com:9000/api/inbound";
        this.smsMethod = "Post";
        this.smsFallbackUrl = "";
        this.smsFallbackMethod = "";
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public String getHangupCallbackUrl() {
        return hangupCallbackUrl;
    }

    public void setHangupCallbackUrl(String hangupCallbackUrl) {
        this.hangupCallbackUrl = hangupCallbackUrl;
    }

    public String getHangupCallbackMethod() {
        return hangupCallbackMethod;
    }

    public void setHangupCallbackMethod(String hangupCallbackMethod) {
        this.hangupCallbackMethod = hangupCallbackMethod;
    }

    public String getHeartbeatUrl() {
        return heartbeatUrl;
    }

    public void setHeartbeatUrl(String heartbeatUrl) {
        this.heartbeatUrl = heartbeatUrl;
    }

    public String getHeartbeatMethod() {
        return heartbeatMethod;
    }

    public void setHeartbeatMethod(String heartbeatMethod) {
        this.heartbeatMethod = heartbeatMethod;
    }

    public String getVoiceUrl() {
        return voiceUrl;
    }

    public void setVoiceUrl(String voiceUrl) {
        this.voiceUrl = voiceUrl;
    }

    public String getVoiceMethod() {
        return voiceMethod;
    }

    public void setVoiceMethod(String voiceMethod) {
        this.voiceMethod = voiceMethod;
    }

    public String getVoiceFallbackUrl() {
        return voiceFallbackUrl;
    }

    public void setVoiceFallbackUrl(String voiceFallbackUrl) {
        this.voiceFallbackUrl = voiceFallbackUrl;
    }

    public String getVoiceFallbackMethod() {
        return voiceFallbackMethod;
    }

    public void setVoiceFallbackMethod(String voiceFallbackMethod) {
        this.voiceFallbackMethod = voiceFallbackMethod;
    }

    public String getSmsUrl() {
        return smsUrl;
    }

    public void setSmsUrl(String smsUrl) {
        this.smsUrl = smsUrl;
    }

    public String getSmsMethod() {
        return smsMethod;
    }

    public void setSmsMethod(String smsMethod) {
        this.smsMethod = smsMethod;
    }

    public String getSmsFallbackUrl() {
        return smsFallbackUrl;
    }

    public void setSmsFallbackUrl(String smsFallbackUrl) {
        this.smsFallbackUrl = smsFallbackUrl;
    }

    public String getSmsFallbackMethod() {
        return smsFallbackMethod;
    }

    public void setSmsFallbackMethod(String smsFallbackMethod) {
        this.smsFallbackMethod = smsFallbackMethod;
    }
}
