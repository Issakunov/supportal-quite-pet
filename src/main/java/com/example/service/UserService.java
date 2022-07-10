package com.example.service;

import com.example.domain.Users;
import com.example.exception.EmailExistsException;
import com.example.exception.EmailNotFoundException;
import com.example.exception.UsernameExistsException;

import java.util.List;

public interface UserService {

    Users register(String firstname, String lastname, String username, String email) throws EmailNotFoundException, UsernameExistsException, EmailExistsException;
    List<Users> getUsers();
    Users findUserByUsername(String username);
    Users findUserByEmail(String email) throws EmailNotFoundException;

}
