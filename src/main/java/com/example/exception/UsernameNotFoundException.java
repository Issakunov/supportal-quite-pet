package com.example.exception;

public class UsernameNotFoundException extends Exception{

    public UsernameNotFoundException(String message) {
        super(message);
    }
}
