package model;

public class CommonResponse {
    
    private ResponseStatus status;
    private String message;
    private Object data;

    public CommonResponse() {}

    private CommonResponse(ResponseStatus status,
                           String message,
                           Object data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public ResponseStatus getStatus() { return status; }
    public void setStatus(ResponseStatus status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }

    public static CommonResponse OK(String message, Object data) {
        return new CommonResponse(ResponseStatus.OK, message, data);
    }

    public static CommonResponse OK(Object data) {
        return OK(null, data);
    }

    public static CommonResponse OK(String message) {
        return OK(message, null);
    }

    public static CommonResponse OK() {
        return OK(null, null);
    }


    public static CommonResponse WARNING(String message, Object data) {
        return new CommonResponse(ResponseStatus.WARNING, message, data);
    }

    public static CommonResponse WARNING(Object data) {
        return WARNING(null, data);
    }

    public static CommonResponse WARNING(String message) {
        return WARNING(message, null);
    }

    public static CommonResponse WARNING() {
        return WARNING(null, null);
    }


    public static CommonResponse ERROR(String message, Object data) {
        return new CommonResponse(ResponseStatus.ERROR, message, data);
    }

    public static CommonResponse ERROR(Object data) {
        return ERROR(null, data);
    }

    public static CommonResponse ERROR(String message) {
        return ERROR(message, null);
    }

    public static CommonResponse ERROR() {
        return ERROR(null, null);
    }
}