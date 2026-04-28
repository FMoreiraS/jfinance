package com.jfinance.data;

public class Transaction {
    TransactionType type;
    String name;
    String optDescription;
    double value;

    public Transaction() {}

    public Transaction(TransactionType type, String name, double value) {
        this.type = type;
        this.name = name;
        this.value = value;
    }

    public Transaction(TransactionType type, String name, double value, String description) {
        this(type, name, value);
        this.optDescription = description;
    }
}
