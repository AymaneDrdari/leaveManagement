package net.atos.jourferie.exception;

public class RessourceNotFoundException extends RuntimeException{
    public RessourceNotFoundException(String message){
        super(message);
    }
}