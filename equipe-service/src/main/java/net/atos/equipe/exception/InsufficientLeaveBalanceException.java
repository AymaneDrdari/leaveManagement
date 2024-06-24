package net.atos.equipe.exception;

public class InsufficientLeaveBalanceException extends RuntimeException {
    public InsufficientLeaveBalanceException(String message) {
        super(message);
    }
}
