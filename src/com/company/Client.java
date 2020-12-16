package com.company;

import java.io.Serializable;
import java.util.ArrayList;

public class Client implements Serializable {
    private final String name;
    private final ArrayList<Card> cards = new ArrayList<>();

    public Client(String name, Card card) {
        this.name = name;
        this.cards.add(card);
    }

    public String getName() {
        return this.name;
    }

    public ArrayList<Card> getCards() {
        return this.cards;
    }

    public void addCard(Card card){
        this.cards.add(card);
    }

    public void deleteCard(int index){
        this.cards.remove(index);
    }
}
