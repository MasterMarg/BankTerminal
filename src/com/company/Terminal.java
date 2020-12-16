package com.company;

import com.company.Exceptions.InvalidSumException;
import com.company.Exceptions.OutOfMoneyException;
import com.company.Exceptions.UsernameIsAlreadyTakenException;

public interface Terminal {

    int getBalance(Card card);

    void addMoney(Card card, int money) throws InvalidSumException;

    void takeMoney(Card card, int money) throws InvalidSumException, OutOfMoneyException;

    String[] createClient(String name) throws UsernameIsAlreadyTakenException;

    void deleteClient(Client client);

    String[] createCard(Client client);

    boolean deleteCard(Client client, Card card, int index);
}
