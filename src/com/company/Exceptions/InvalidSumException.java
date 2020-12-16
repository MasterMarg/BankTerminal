package com.company.Exceptions;

public class InvalidSumException extends Exception {
    private static final String message = "Сумма должна быть не меньше и кратна 100!";

    public InvalidSumException(){
        super(message);
    }
}
