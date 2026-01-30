package com.example.auth.exception;

public class MissingEmailAndOtpException extends RuntimeException{
    public MissingEmailAndOtpException(String message){
        super(message);
    }
}
