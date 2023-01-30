package com.jossemar.wallet.microservice.exception;

public class InvalidDataException extends RuntimeException {

    public InvalidDataException(String message){
        super(message);
    }
}
