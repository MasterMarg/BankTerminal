package com.company;

import com.company.Exceptions.*;

import java.io.Serializable;
import java.util.ArrayList;

public class TerminalBase implements Terminal, Serializable {
    private final ArrayList<Client> clients;

    public TerminalBase() {
        clients = new ArrayList<>();
    }

    ArrayList<Client> getClients() {
        return this.clients;
    }

    public int getBalance(Card card) {
        return card.getBalance();
    }

    public void addMoney(Card card, int money) throws InvalidSumException {
        if (money % 100 != 0 || money < 100) throw new InvalidSumException();
        card.refillBalance(money);
    }

    public void takeMoney(Card card, int money) throws InvalidSumException, OutOfMoneyException {
        if (money % 100 != 0 || money < 100) throw new InvalidSumException();
        if (money > card.getBalance()) throw new OutOfMoneyException();
        card.getMoney(money);
    }

    public String[] createClient(String name) throws UsernameIsAlreadyTakenException {
        for (Client client : this.clients) {
            if (client.getName().equals(name)) throw new UsernameIsAlreadyTakenException();
        }
        Card card = generateCard();
        Client client = new Client(name, card);
        this.clients.add(client);
        String[] log = new String[5];
        log[0] = "\n******************************";
        log[4] = "******************************";
        log[1] = "Имя пользователя: \u001b[30;1m" + client.getName();
        log[2] = "Номер карты: \u001b[30;1m" + card.getCardNumber();
        log[3] = "PIN-код: \u001b[30;1m" + card.getPinCode();
        return log;
    }

    public void deleteClient(Client client) {
        this.clients.remove(client);
    }

    public String[] createCard(Client client) {
        Card card = generateCard();
        client.addCard(card);
        String[] log = new String[4];
        log[0] = "\n******************************";
        log[3] = "******************************";
        log[1] = "Номер карты: \u001b[30;1m" + card.getCardNumber();
        log[2] = "PIN-код: \u001b[30;1m" + card.getPinCode();
        return log;
    }

    private Card generateCard() {
        boolean isCardNumberChosen = false;
        StringBuilder cardNumber = new StringBuilder("4");
        while (!isCardNumberChosen) {
            for (int index = 0; index < 15; index++) {
                cardNumber.append((int) (Math.random() * 10));
            }
            try {
                for (Client client : this.clients) {
                    for (Card card : client.getCards()) {
                        if (cardNumber.toString().equals(card.getCardNumber()))
                            throw new CardNumberIsNotAvailableException();
                    }
                }
                isCardNumberChosen = true;
            } catch (CardNumberIsNotAvailableException exception) {
                cardNumber = new StringBuilder("4");
            }
        }
        return new Card(cardNumber.toString());
    }

    public Object[] getClientData(String cardNumber) throws UserNotFoundException {
        for (Client client : this.clients) {
            for (Card card : client.getCards()) {
                if (cardNumber.equals(card.getCardNumber())) {
                    return new Object[]{client, card};
                }
            }
        }
        throw new UserNotFoundException();
    }

    public boolean deleteCard(Client client, Card card, int index) {
        boolean outcome = false;
        if (card.getCardNumber().equals(client.getCards().get(index).getCardNumber())) outcome = true;
        client.deleteCard(index);
        return outcome;
    }
}
