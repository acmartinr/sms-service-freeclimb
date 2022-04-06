package model;

public class ResetPasswordRequest {

    private String phone;
    private Integer code;
    private String password;

    public ResetPasswordRequest() {}

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

}
