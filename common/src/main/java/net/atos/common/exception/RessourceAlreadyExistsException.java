package net.atos.common.exception;

public class RessourceAlreadyExistsException extends RuntimeException{
    public RessourceAlreadyExistsException(String message) {
        super(message);
    }
}