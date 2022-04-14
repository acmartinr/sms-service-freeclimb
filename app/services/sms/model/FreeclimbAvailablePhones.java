package services.sms.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FreeclimbAvailablePhones {


    @JsonProperty("total")
    String total;
    @JsonProperty("end")
    String end;
    @JsonProperty("page")
    String page;
    @JsonProperty("numPages")
    String numPages;
    @JsonProperty("pageSize")
    String pageSize;
    @JsonProperty("nextPageUri")
    String nextPageUri;
    @JsonProperty("availablePhoneNumbers")
    List<FreeClimbPhoneNumber> availablePhoneNumbers;

    public FreeclimbAvailablePhones() {
        this.total = "";
        this.end = "";
        this.page = "";
        this.numPages = "";
        this.pageSize = "";
        this.nextPageUri = "";
        this.availablePhoneNumbers = new ArrayList<>();
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getNumPages() {
        return numPages;
    }

    public void setNumPages(String numPages) {
        this.numPages = numPages;
    }

    public String getPageSize() {
        return pageSize;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    public String getNextPageUri() {
        return nextPageUri;
    }

    public void setNextPageUri(String nextPageUri) {
        this.nextPageUri = nextPageUri;
    }

    public List<FreeClimbPhoneNumber> getAvailablePhoneNumbers() {
        return availablePhoneNumbers;
    }

    public void setAvailablePhoneNumbers(List<FreeClimbPhoneNumber> availablePhoneNumbers) {
        this.availablePhoneNumbers = availablePhoneNumbers;
    }
}
