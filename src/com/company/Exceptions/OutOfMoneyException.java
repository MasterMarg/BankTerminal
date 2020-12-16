package com.company.Exceptions;

public class OutOfMoneyException extends Exception{
    private static final String message = "Недостаточно средств на счету!";

    public OutOfMoneyException(){
        super(message);
    }
}
