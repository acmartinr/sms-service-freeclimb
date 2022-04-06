package model;

public class Statistics {

    private int active;
    private int total;
    private int sent;

    public Statistics() {}

    public Statistics(int active,
                      int total,
                      int sent) {
        this.active = active;
        this.total = total;
        this.sent = sent;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getSent() {
        return sent;
    }

    public void setSent(int sent) {
        this.sent = sent;
    }
}
