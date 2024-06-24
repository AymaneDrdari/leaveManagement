package net.atos.conge.exception;

public class RessourceAlreadyExistsException extends RuntimeException{
    public RessourceAlreadyExistsException(String message) {
        super(message);
    }
}