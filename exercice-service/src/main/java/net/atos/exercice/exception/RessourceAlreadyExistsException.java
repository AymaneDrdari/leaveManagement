package net.atos.exercice.exception;

public class RessourceAlreadyExistsException extends RuntimeException{
    public RessourceAlreadyExistsException(String message) {
        super(message);
    }
}