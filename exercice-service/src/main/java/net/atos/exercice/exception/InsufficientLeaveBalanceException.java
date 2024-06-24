package net.atos.exercice.exception;

public class InsufficientLeaveBalanceException extends RuntimeException {
    public InsufficientLeaveBalanceException(String message) {
        super(message);
    }
}
