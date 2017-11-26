package ua.edu.lnu.dcservice.exceptions;

public class ServiceException extends RuntimeException {
    public ServiceException(String msg, Object... args) {
        super(String.format(msg, args));
    }
}
