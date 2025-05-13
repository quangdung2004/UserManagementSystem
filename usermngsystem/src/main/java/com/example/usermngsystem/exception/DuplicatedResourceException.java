package com.example.usermngsystem.exception;

public class DuplicatedResourceException extends RuntimeException{
    public DuplicatedResourceException(String message){
        super(message);
    }
}
