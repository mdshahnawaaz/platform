package com.logistic.platform.models;

public class ProductRequest {

    private Long amount;
    private Long quantity;
    private String id;
    private String currency;

    public ProductRequest() {
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Override
    public String toString() {
        return "ProductRequest{" +
                "amount=" + amount +
                ", quantity=" + quantity +
                ", id='" + id + '\'' +
                ", currency='" + currency + '\'' +
                '}';
    }
}
