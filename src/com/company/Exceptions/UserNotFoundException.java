package com.company.Exceptions;

import java.io.IOException;

public class UserNotFoundException extends IOException {
    private static final String message = "Карта не зарегистрирована в системе!";

    public UserNotFoundException(){
        super(message);
    }
}
