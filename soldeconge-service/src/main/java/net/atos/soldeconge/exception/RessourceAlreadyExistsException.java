package net.atos.soldeconge.exception;

public class RessourceAlreadyExistsException extends RuntimeException{
    public RessourceAlreadyExistsException(String message) {
        super(message);
    }
}