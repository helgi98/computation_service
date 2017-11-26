package ua.edu.lnu.dcservice.exceptions;

public class ValidationException extends RuntimeException {
    public ValidationException(String msg, Object... args) {
        super(String.format(msg, args));
    }
}
