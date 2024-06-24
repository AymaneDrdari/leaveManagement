package net.atos.equipe.exception;

public class RessourceAlreadyExistsException extends RuntimeException{
    public RessourceAlreadyExistsException(String message) {
        super(message);
    }
}