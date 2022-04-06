package services.database.model;

public class CampaignListItem {

    private long id;
    private long listId;
    private long phone;
    private boolean sent;
    private String data;
    private String data2;

    public CampaignListItem() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getListId() {
        return listId;
    }

    public void setListId(long listId) {
        this.listId = listId;
    }

    public long getPhone() {
        String strValue = new Long(phone).toString();
        if (!strValue.startsWith("1")) {
            strValue = "1" + strValue;
        }

        return Long.parseLong(strValue);
    }

    public void setPhone(long phone) {
        this.phone = phone;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getData2() {
        return data2;
    }

    public void setData2(String data2) {
        this.data2 = data2;
    }
}
