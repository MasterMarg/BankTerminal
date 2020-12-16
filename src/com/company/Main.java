package com.company;

import com.company.Exceptions.*;

import java.io.*;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Scanner;

public class Main {
    static String ANSI_BOLD_WHITE = "\u001b[30;1m";
    static String ANSI_BOLD_RED = "\u001b[31;1m";
    static String ANSI_BOLD = "\u001b[0;1m";
    static String ANSI_RESET = "\u001b[0m";
    static String[] mainMenu = {"Возможности:",
            "1. Регистрация",
            "2. Вход в сеть",
            "3. Распечатать базу",
            "4. Выключить свет"};
    static String[] menu2 = {"Возможности:",
            "1. Пополнить баланс карты",
            "2. Проверить баланс карты",
            "3. Снять деньги",
            "4. Завести новую карту",
            "5. Удалить карту",
            "6. Удалить пользователя",
            "7. Вернуть карту"};

    public static void main(String[] args) {
        TerminalBase terminalBase;
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream("Data.xml"));
            terminalBase = (TerminalBase) in.readObject();
            in.close();
        } catch (FileNotFoundException exception) {
            terminalBase = new TerminalBase();
        } catch (ClassNotFoundException | IOException exception) {
            System.out.println(exception.getMessage());
            terminalBase = new TerminalBase();
        }
        Scanner scanner = new Scanner(System.in);
        boolean outOfEnergy = false;
        boolean isThereInputError = false;
        int menuErrorCounter = 0;
        int pinErrorCounter = 0;
        Calendar calendar = null;
        while (!outOfEnergy) {
            System.out.println();
            if (terminalBase.getClients().isEmpty()) {
                System.out.println(ANSI_BOLD + "Зарегистрируйтесь!" + ANSI_RESET);
                createClient(terminalBase, scanner);
            }
            if (!isThereInputError) {
                for (String string : mainMenu) System.out.println(ANSI_BOLD + string + ANSI_RESET);
                menuErrorCounter = 0;
            }
            isThereInputError = false;
            switch (scanner.nextInt()) {
                case 1 -> {
                    createClient(terminalBase, scanner);
                    pinErrorCounter = 0;
                }
                case 2 -> {
                    try {
                        if (pinErrorCounter == 3) {
                            Date endDate = calendar.getTime();
                            Date currentDate = new Date();
                            if (currentDate.before(endDate)) {
                                long timeInSecs = (endDate.getTime() - currentDate.getTime()) / 1000;
                                throw new AccountBlockedException(timeInSecs);
                            }
                            pinErrorCounter = 0;
                        }
                        logIn(terminalBase);
                    } catch (UserNotFoundException exception) {
                        System.out.println(ANSI_BOLD_RED + exception.getMessage() + ANSI_RESET);
                        pinErrorCounter = 0;
                    } catch (AccountBlockedException exception) {
                        System.out.println(ANSI_BOLD_RED + exception.getMessage() + ANSI_RESET);
                    } catch (WrongPinCodeException exception) {
                        try {
                            pinErrorCounter = pinErrorCounter + 1;
                            if (pinErrorCounter == 3) throw new AccountBlockedException(10);
                            System.out.println(ANSI_BOLD_RED + exception.getMessage() + ANSI_RESET);
                        } catch (AccountBlockedException exception1) {
                            calendar = new GregorianCalendar();
                            calendar.add(Calendar.SECOND, 10);
                            System.out.println(ANSI_BOLD_RED + exception1.getMessage() + ANSI_RESET);
                        }
                    }
                }
                case 3 -> {
                    pinErrorCounter = 0;
                    if (!terminalBase.getClients().isEmpty()) try {
                        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("Base.txt")));
                        for (Client client : terminalBase.getClients()) {
                            writer.println(client.getName());
                            for (Card card : client.getCards()) {
                                writer.println(card.getCardNumber() + "   " + card.getPinCode());
                            }
                            writer.println();
                        }
                        writer.close();
                        System.out.println(ANSI_BOLD + "База клиентов сохранена в документ Base.txt" + ANSI_RESET);
                    } catch (IOException exception) {
                        System.out.println(exception.getMessage());
                    }
                    else {
                        System.out.println(ANSI_BOLD_RED + "База пуста!" + ANSI_RESET);
                    }
                }
                case 4 -> {
                    try {
                        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("Data.xml"));
                        out.writeObject(terminalBase);
                        out.close();
                    } catch (IOException exception) {
                        System.out.println(exception.getMessage());
                    }
                    System.out.println(ANSI_BOLD + "Выключаюсь... пхххфффф...." + ANSI_RESET);
                    outOfEnergy = true;
                }
                default -> {
                    menuErrorCounter = menuErrorCounter + 1;
                    if (menuErrorCounter == 3) System.out.println(ANSI_BOLD_RED + "Ошибка ввода!" + ANSI_RESET);
                    else {
                        System.out.println(ANSI_BOLD_RED + "Ошибка! Повторите ввод:" + ANSI_RESET);
                        isThereInputError = true;
                    }
                }
            }
        }
        scanner.close();
    }

    public static void createClient(TerminalBase terminalBase, Scanner scanner) {
        boolean isNameChosen = false;
        System.out.println(ANSI_BOLD + "Выберите имя:" + ANSI_RESET);
        while (!isNameChosen) {
            String name = scanner.next();
            try {
                for (String string : terminalBase.createClient(name))
                    System.out.println(ANSI_BOLD + string + ANSI_RESET);
                isNameChosen = true;
            } catch (UsernameIsAlreadyTakenException exception) {
                System.out.println(ANSI_BOLD_RED + exception.getMessage() + ANSI_RESET);
            }
        }
    }

    public static void logIn(TerminalBase terminalBase) throws UserNotFoundException, WrongPinCodeException {
        String cardNumber = null;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("Card.txt"));
            cardNumber = bufferedReader.readLine();
            bufferedReader.close();
        } catch (IOException exception) {
            System.out.println(ANSI_BOLD_RED + exception.getMessage() + ANSI_RESET);
        }
        Object[] clientData = terminalBase.getClientData(cardNumber);
        Client theClient = (Client) clientData[0];
        Card theCard = (Card) clientData[1];
        System.out.println(ANSI_BOLD + "Введите ПИН-код:" + ANSI_RESET);
        Scanner scanner = new Scanner(System.in);
        String pinCode = scanner.nextLine();
        if (!pinCode.equals(theCard.getPinCode())) throw new WrongPinCodeException();
        boolean isDoneWorking = false;
        boolean isThereInputError = false;
        int menuErrorCounter = 0;
        while (!isDoneWorking) {
            if (!isThereInputError) {
                System.out.println(ANSI_BOLD + "\nДобро пожаловать, " + ANSI_BOLD_WHITE +
                        theClient.getName() + ANSI_RESET + "!");
                System.out.println();
                for (String string : menu2) System.out.println(ANSI_BOLD + string + ANSI_RESET);
                menuErrorCounter = 0;
            }
            isThereInputError = false;
            switch (scanner.nextInt()) {
                case 1 -> {
                    System.out.println(ANSI_BOLD + "Внесите деньги (сумма должна быть кратна 100)" + ANSI_RESET);
                    int money = scanner.nextInt();
                    try {
                        terminalBase.addMoney(theCard, money);
                    } catch (InvalidSumException exception) {
                        System.out.println(ANSI_BOLD_RED + exception.getMessage() + ANSI_RESET);
                    }
                }
                case 2 -> System.out.println(ANSI_BOLD + "Ваш баланс " + ANSI_BOLD_WHITE +
                        terminalBase.getBalance(theCard) + ANSI_RESET);
                case 3 -> {
                    System.out.println(ANSI_BOLD + "Введите ПИН-код" + ANSI_RESET);
                    String pinCode2 = scanner.nextLine();
                    if (!pinCode2.equals(theCard.getPinCode())) throw new WrongPinCodeException();
                    System.out.println(ANSI_BOLD + "Введите сумму (сумма должна быть кратна 100)" + ANSI_RESET);
                    int money = scanner.nextInt();
                    try {
                        terminalBase.takeMoney(theCard, money);
                    } catch (InvalidSumException | OutOfMoneyException exception) {
                        System.out.println(ANSI_BOLD_RED + exception.getMessage() + ANSI_RESET);
                    }
                }
                case 4 -> {
                    for (String string : terminalBase.createCard(theClient))
                        System.out.println(ANSI_BOLD + string + ANSI_RESET);
                }
                case 5 -> {
                    System.out.println(ANSI_BOLD + "Выберите карту для удаления:" + ANSI_RESET);
                    int counter = 0;
                    for (Card card : theClient.getCards()) {
                        counter = counter + 1;
                        System.out.println(ANSI_BOLD + counter + ". " + ANSI_BOLD_WHITE +
                                card.getCardNumber() + ANSI_RESET);
                    }
                    System.out.println(ANSI_BOLD + (counter + 1) + ". Отмена" + ANSI_RESET);
                    int choice = scanner.nextInt();
                    if (choice > 0 && choice <= theClient.getCards().size()) {
                        if (terminalBase.deleteCard(theClient, theCard, choice - 1)) isDoneWorking = true;
                    } else if (choice != theClient.getCards().size() + 1)
                        System.out.println(ANSI_BOLD_RED + "Ошибка ввода!" + ANSI_RESET);
                }
                case 6 -> {
                    isDoneWorking = true;
                    terminalBase.deleteClient(theClient);
                }
                case 7 -> isDoneWorking = true;
                default -> {
                    menuErrorCounter = menuErrorCounter + 1;
                    if (menuErrorCounter == 3) System.out.println(ANSI_BOLD_RED + "Ошибка ввода!" + ANSI_RESET);
                    else {
                        System.out.println(ANSI_BOLD_RED + "Ошибка! Повторите ввод:" + ANSI_RESET);
                        isThereInputError = true;
                    }
                }
            }
        }
    }
}
