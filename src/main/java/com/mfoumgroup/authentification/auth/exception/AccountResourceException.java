package com.mfoumgroup.authentification.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class AccountResourceException extends RuntimeException{

    public AccountResourceException(String message){
        super(message);
    }
}
