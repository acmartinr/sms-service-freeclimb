package model;

public class RegisterRequest {

    private String phone;
    private Integer code;
    private String password;
    private String fullName;
    private Integer resellerNumber;

    public RegisterRequest() {}

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

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Integer getResellerNumber() {
        return resellerNumber;
    }

    public void setResellerNumber(Integer resellerNumber) {
        this.resellerNumber = resellerNumber;
    }
}
