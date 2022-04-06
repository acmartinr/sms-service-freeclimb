package model;

public class TransactionStatistics {

    private float amount;
    private int count;
    private int type;

    public TransactionStatistics() {}

    public TransactionStatistics(float amount, int count, int type) {
        this.amount = amount;
        this.count = count;
        this.type = type;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void update(int count, float amount) {
        this.count = this.count + count;
        this.amount = this.amount + amount;
    }
}
