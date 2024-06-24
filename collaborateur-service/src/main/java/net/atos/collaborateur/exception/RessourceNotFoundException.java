package net.atos.collaborateur.exception;

public class RessourceNotFoundException extends RuntimeException{
    public RessourceNotFoundException(String message){
        super(message);
    }
}