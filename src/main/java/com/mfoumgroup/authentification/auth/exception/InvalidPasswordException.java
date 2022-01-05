package com.mfoumgroup.authentification.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidPasswordException extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public InvalidPasswordException() {
        super(ErrorConstants.INVALID_PASSWORD_TYPE, "Incorrect password!", "userManagement", "invalidpassword");
    }
}
