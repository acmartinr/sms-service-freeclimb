package model;

public class CommonRequest {

    private long userId;
    private int page;
    private int limit;
    private int offset;
    private String search;
    private long dateFrom;
    private long dateTo;
    private String agentUsername;
    private Long campaignId;
    private Long resellerId;
    private String order;
    private boolean orderDesc;
    private long startDate;
    private long endDate;
    private Long phoneFrom;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getOffset() {
        return page * limit;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getSearch() {
        return search != null && search.length() > 0 ? "%" + search + "%" : "%%";
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public CommonRequest() {
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(long dateFrom) {
        this.dateFrom = dateFrom;
    }

    public long getDateTo() {
        return dateTo;
    }

    public void setDateTo(long dateTo) {
        this.dateTo = dateTo;
    }

    public String getAgentUsername() {
        return agentUsername;
    }

    public void setAgentUsername(String agentUsername) {
        this.agentUsername = agentUsername;
    }

    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    public Long getResellerId() {
        return resellerId;
    }

    public void setResellerId(Long resellerId) {
        this.resellerId = resellerId;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public boolean isOrderDesc() {
        return orderDesc;
    }

    public void setOrderDesc(boolean orderDesc) {
        this.orderDesc = orderDesc;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public Long getPhoneFrom() {
        return phoneFrom;
    }

    public void setPhoneFrom(Long phoneFrom) {
        this.phoneFrom = phoneFrom;
    }
}
