package com.company.Exceptions;

import java.io.IOException;

public class UsernameIsAlreadyTakenException extends IOException {
    private static final String message = "Пользователь с таким именем уже существует!\nВыберите другое имя:";

    public UsernameIsAlreadyTakenException(){
        super(message);
    }
}
