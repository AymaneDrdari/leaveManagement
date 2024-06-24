package net.atos.exercice.exception;

public class RessourceNotFoundException extends RuntimeException{
    public RessourceNotFoundException(String message){
        super(message);
    }
}