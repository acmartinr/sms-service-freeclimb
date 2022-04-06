package services.database.model;

import org.apache.commons.text.WordUtils;

import java.util.LinkedList;
import java.util.List;

public class Campaign {

    private long id;
    private String name;
    private String message;
    private int status;
    private int senderType;
    private int senderGroup;
    private Long userId;
    private long leadsCount;
    private long sentCount;
    private long date;
    private long startDate;
    private long endDate;
    private int startTime;
    private int endTime;
    private int startLocalTime;
    private int endLocalTime;
    private boolean monday;
    private boolean tuesday;
    private boolean wednesday;
    private boolean thursday;
    private boolean friday;
    private boolean saturday;
    private boolean sunday;
    private String errorStatus;
    private long dncCount;
    private long errorsCount;
    private long ignoredCount;
    private String agentUsername;
    private String agentPassword;
    private boolean allPhones;
    private boolean filterDNC;


    private String campaigntype;

    private List<CampaignList> lists = new LinkedList();
    private List<Phone> selectedPhones = new LinkedList();

    public Campaign() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public String getFormattedMessage(String data, String data2) {
        if (data == null) { data = ""; }
        if (data2 == null) { data2 = ""; }

        data = WordUtils.capitalizeFully(data);

        return message
                .replace("<POPULATE>", data)
                .replace("<POPULATE1>", data)
                .replace("<POPULATE2>", data2);
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }
    public String getCampaigntype() {
        return campaigntype;
    }

    public void setCampaigntype(String campaigntype) {
        this.campaigntype = campaigntype;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getSenderType() {
        return senderType;
    }

    public void setSenderType(int senderType) {
        this.senderType = senderType;
    }

    public int getSenderGroup() {
        return senderGroup;
    }

    public void setSenderGroup(int senderGroup) {
        this.senderGroup = senderGroup;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getLeadsCount() {
        return leadsCount;
    }

    public void setLeadsCount(long leadsCount) {
        this.leadsCount = leadsCount;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
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

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public int getStartLocalTime() {
        return startLocalTime;
    }

    public void setStartLocalTime(int startLocalTime) {
        this.startLocalTime = startLocalTime;
    }

    public int getEndLocalTime() {
        return endLocalTime;
    }

    public void setEndLocalTime(int endLocalTime) {
        this.endLocalTime = endLocalTime;
    }

    public boolean isMonday() {
        return monday;
    }

    public void setMonday(boolean monday) {
        this.monday = monday;
    }

    public boolean isTuesday() {
        return tuesday;
    }

    public void setTuesday(boolean tuesday) {
        this.tuesday = tuesday;
    }

    public boolean isWednesday() {
        return wednesday;
    }

    public void setWednesday(boolean wednesday) {
        this.wednesday = wednesday;
    }

    public boolean isThursday() {
        return thursday;
    }

    public void setThursday(boolean thursday) {
        this.thursday = thursday;
    }

    public boolean isFriday() {
        return friday;
    }

    public void setFriday(boolean friday) {
        this.friday = friday;
    }

    public boolean isSaturday() {
        return saturday;
    }

    public void setSaturday(boolean saturday) {
        this.saturday = saturday;
    }

    public boolean isSunday() {
        return sunday;
    }

    public void setSunday(boolean sunday) {
        this.sunday = sunday;
    }

    public long getSentCount() {
        return sentCount;
    }

    public void setSentCount(long sentCount) {
        this.sentCount = sentCount;
    }

    public List<CampaignList> getLists() {
        return lists;
    }

    public void setLists(List<CampaignList> lists) {
        this.lists = lists;
    }

    public String getErrorStatus() {
        return errorStatus;
    }

    public void setErrorStatus(String errorStatus) {
        this.errorStatus = errorStatus;
    }

    public List<Phone> getSelectedPhones() {
        return selectedPhones;
    }

    public void setSelectedPhones(List<Phone> selectedPhones) {
        this.selectedPhones = selectedPhones;
    }

    public long getDncCount() {
        return dncCount;
    }

    public void setDncCount(long dncCount) {
        this.dncCount = dncCount;
    }

    public long getErrorsCount() {
        return errorsCount;
    }

    public void setErrorsCount(long errorsCount) {
        this.errorsCount = errorsCount;
    }

    public long getIgnoredCount() {
        return ignoredCount;
    }

    public void setIgnoredCount(long ignoredCount) {
        this.ignoredCount = ignoredCount;
    }

    public String getAgentUsername() {
        return agentUsername;
    }

    public void setAgentUsername(String agentUsername) {
        this.agentUsername = agentUsername;
    }

    public String getAgentPassword() {
        return agentPassword;
    }

    public void setAgentPassword(String agentPassword) {
        this.agentPassword = agentPassword;
    }

    public boolean isAllPhones() {
        return allPhones;
    }

    public void setAllPhones(boolean allPhones) {
        this.allPhones = allPhones;
    }

    public boolean isFilterDNC() {
        return filterDNC;
    }

    public void setFilterDNC(boolean filterDNC) {
        this.filterDNC = filterDNC;
    }
}
