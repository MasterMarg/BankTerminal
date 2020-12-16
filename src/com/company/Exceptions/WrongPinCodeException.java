package com.company.Exceptions;

import java.io.IOException;

public class WrongPinCodeException extends IOException {
    private static final String message = "Неверный PIN-код!\n";

    public WrongPinCodeException(){
        super(message);
    }
}
