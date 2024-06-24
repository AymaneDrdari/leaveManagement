package net.atos.soldeconge.exception;

public class RessourceNotFoundException extends RuntimeException{
    public RessourceNotFoundException(String message){
        super(message);
    }
}