package net.atos.collaborateur.exception;

public class InsufficientLeaveBalanceException extends RuntimeException {
    public InsufficientLeaveBalanceException(String message) {
        super(message);
    }
}
