package com.sebray.jwt.exception;

public class InvalidArgumentException extends RuntimeException{
    public InvalidArgumentException(String message){
        super(message);
    }
}
