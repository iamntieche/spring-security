package com.mfoumgroup.authentification.interceptors;

import com.mfoumgroup.authentification.auth.exception.BadRequestAlertException;
import com.mfoumgroup.authentification.auth.exception.UsernameAlreadyUsedException;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class Validators {

    private static final  String ERRORS = "errors";

    /**
     * retourne l'object d'erreur en cas de non validation des données
     */
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, Map<String, String>> handleValidationUnprocessableEntityExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errorsFields = new HashMap<>();
        ex.getBindingResult().getAllErrors().
                forEach(error -> errorsFields.put(((FieldError) error).getField(), error.getDefaultMessage()));
        Map<String, Map<String, String>> errors = new HashMap<>();
        errors.put(ERRORS, errorsFields);
        return errors;
    }

    /**
     * retourne l'object d'erreur en cas de non validation des données
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, Map<String, String>> handleValidationBadRequestEntityExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errorsFields = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> errorsFields.put(((FieldError) error).getField(), error.getDefaultMessage()));
        Map<String, Map<String, String>> errors = new HashMap<>();
        errors.put(ERRORS, errorsFields);
        return errors;
    }

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(BadRequestAlertException.class)
	public Map<String, String> handleValidationBadRequestAlertExceptionExceptions(BadRequestAlertException ex) {
		Map<String, String> errorsFields = new HashMap<>();
		errorsFields.put(ERRORS, ex.getMessage());
		return errorsFields;
	}

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleMethodServerError(RuntimeException ex) {
        Map<String, String> errorsFields = new HashMap<>();
        errorsFields.put(ERRORS, ex.getMessage());
        return errorsFields;
    }
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleMethodConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> errorsFields = new HashMap<>();
        errorsFields.put(ERRORS, ex.getMessage());
        return errorsFields;
    }
    @ExceptionHandler(MalformedJwtException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleMethodServletException(MalformedJwtException ex) {
        Map<String, String> errorsFields = new HashMap<>();
        errorsFields.put(ERRORS, ex.getMessage());
        return errorsFields;
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public  Map<String, Map<String, String>> handleMethodUsernameNotFoundException(UsernameNotFoundException ex) {
        Map<String, String> errorsFields = new HashMap<>();
        errorsFields.put("email", ex.getMessage());
        Map<String, Map<String, String>> errors = new HashMap<>();
        errors.put(ERRORS, errorsFields);
        return errors;
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public Map<String, String> handleMethodStackOverFlow(HttpRequestMethodNotSupportedException ex) {
        Map<String, String> errorsFields = new HashMap<>();
        errorsFields.put(ERRORS, ex.getMessage());
        return errorsFields;
    }

    @ExceptionHandler(UsernameAlreadyUsedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public Map<String, String> handleMethodUsernameAlreadyUsed(UsernameAlreadyUsedException ex) {
        Map<String, String> errorsFields = new HashMap<>();
        errorsFields.put(ERRORS, ex.getMessage());
        return errorsFields;
    }


}
