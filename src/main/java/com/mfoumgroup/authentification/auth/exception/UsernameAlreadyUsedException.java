package com.mfoumgroup.authentification.auth.exception;

public class UsernameAlreadyUsedException extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public UsernameAlreadyUsedException() {
        super(ErrorConstants.LOGIN_ALREADY_USED_TYPE, "Login name already used!",  "userManagement","loginExist");
    }
}
