package services.sms.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class FreeclimbSmsListResponse {
    @JsonProperty("total")
    long total;
    @JsonProperty("start")
    long start;
    @JsonProperty("end")
    long end;
    @JsonProperty("page")
    Integer page;
    @JsonProperty("numPages")
    Integer numPages;
    @JsonProperty("pageSize")
    Integer pageSize;
    @JsonProperty("nextPageUri")
    String nextPageUri;
    @JsonProperty("messages")
    List<FreeClimbSendSMSResponse> messages;;

    public FreeclimbSmsListResponse() {
        this.total = 0;
        this.start = 0;
        this.end = 0;
        this.page = 0;
        this.numPages = 0;
        this.pageSize = 0;
        this.nextPageUri = "";
        this.messages = new ArrayList<>();
    }


    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getNumPages() {
        return numPages;
    }

    public void setNumPages(Integer numPages) {
        this.numPages = numPages;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getNextPageUri() {
        return nextPageUri;
    }

    public void setNextPageUri(String nextPageUri) {
        this.nextPageUri = nextPageUri;
    }

    public List<FreeClimbSendSMSResponse> getMessages() {
        return messages;
    }

    public void setMessages(List<FreeClimbSendSMSResponse> messages) {
        this.messages = messages;
    }
}
