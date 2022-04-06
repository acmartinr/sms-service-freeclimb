package model;

import services.database.model.Transaction;
import services.database.model.User;

import java.util.List;

public class TransactionsListsResponse {

    private List<Transaction> transactions;
    private int count;

    public TransactionsListsResponse() {}

    public TransactionsListsResponse(List<Transaction> transactions,
                                     int count) {
        this.transactions = transactions;
        this.count = count;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
