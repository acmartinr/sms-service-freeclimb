package model;

import services.database.model.Payment;

import java.util.List;

public class PaymentsListsResponse {

    private List<Payment> payments;
    private int count;

    public PaymentsListsResponse() {}

    public PaymentsListsResponse(List<Payment> payments,
                              int count) {
        this.payments = payments;
        this.count = count;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

}
