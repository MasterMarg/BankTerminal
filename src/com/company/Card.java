package com.company;

import java.io.Serializable;

public class Card implements Serializable {
    private final String cardNumber;
    private final String pinCode;
    private int balance;

    public Card(String cardNumber) {
        this.cardNumber = cardNumber;
        this.pinCode = generatePinCode();
        this.balance = 0;
    }

    public String getCardNumber() {
        return this.cardNumber;
    }

    public String getPinCode() {
        return this.pinCode;
    }

    private String generatePinCode() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int index = 0; index < 4; index++) {
            stringBuilder.append((int) (Math.random() * 10));
        }
        return stringBuilder.toString();
    }

    public void refillBalance(int money) {
        this.balance = this.balance + money;
    }

    public int getBalance() {
        return this.balance;
    }

    public void getMoney(int money) {
        this.balance = this.balance - money;
    }
}
