package com.example.billmate;

public class Bill {
    String billId;
    String personName;
    String totalAmount;

    public Bill(String billId, String personName, String totalAmount) {
        this.billId = billId;
        this.personName = personName;
        this.totalAmount = totalAmount;
    }

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }
}
