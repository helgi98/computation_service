package ua.edu.lnu.dcservice.tasks.exceptions;

public class TaskCancelException extends RuntimeException {
    public TaskCancelException(String msg, Object... args) {
        super(String.format(msg, args));
    }
}
