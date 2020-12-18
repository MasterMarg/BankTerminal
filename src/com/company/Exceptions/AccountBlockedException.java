package com.company.Exceptions;

import java.io.IOException;

public class AccountBlockedException extends IOException {
    private static final String message =
            "Вы ввели неверный ПИН-код несколько раз подряд.\nДоступ в личный кабинет был ограничен на %s ";
    private final String newMessage;

    public AccountBlockedException(long time) {
        super(String.format(message, time));
        newMessage = String.format((message + provideTheWord(time)), time);
    }

    private String provideTheWord(long time) {
        switch ((int) time) {
            case 1 -> {
                return "секунду.";
            }
            case 2, 3, 4 -> {
                return "секунды.";
            }
            default -> {
                return "секунд.";
            }
        }
    }

    @Override
    public String getMessage() {
        return this.newMessage;
    }
}
