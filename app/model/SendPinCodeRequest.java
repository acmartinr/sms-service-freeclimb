package model;

public class SendPinCodeRequest {

    private String phone;

    public SendPinCodeRequest() {}

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Long getParsedPhone() {
        String cleanPhone = phone
                .replaceAll("\"", "")
                .replaceAll(" ", "")
                .replaceAll("\\(", "")
                .replaceAll("\\)", "")
                .replaceAll("\\+", "")
                .replaceAll("-", "")
                .replaceAll(":", "");
        return Long.parseLong(cleanPhone);
    }

}
