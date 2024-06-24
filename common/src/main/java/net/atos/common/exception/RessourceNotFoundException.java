package net.atos.common.exception;

public class RessourceNotFoundException extends RuntimeException{
    public RessourceNotFoundException(String message){
        super(message);
    }
}