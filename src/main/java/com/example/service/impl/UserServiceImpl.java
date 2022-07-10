package com.example.service.impl;

import com.example.domain.UserPrinciple;
import com.example.domain.Users;
import com.example.exception.EmailExistsException;
import com.example.exception.EmailNotFoundException;
import com.example.exception.UsernameExistsException;
import com.example.exception.UsernameNotFoundException;
import com.example.repository.UserRepository;
import com.example.service.LoginAttemptService;
import com.example.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

import static com.example.constant.UserImplConstant.*;
import static com.example.enumeration.Role.ROLE_USER;

@Service
@Transactional
@Qualifier("UserDetailsServiceName")
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final LoginAttemptService loginAttemptService;


    @Override
    public UserDetails loadUserByUsername(String username) {
        Users user = userRepository.findUsersByUsername(username);
        if (user == null) {
            log.error(NO_USER_FOUNT_BY_USERNAME + username);
            try {
                throw new UsernameNotFoundException(NO_USER_FOUNT_BY_USERNAME + username);
            } catch (UsernameNotFoundException e) {
                throw new RuntimeException(e);
            }
        }else {
            validateNewUser(user);
            user.setLastLoginDateDisplay(user.getLastLoginDate());
            user.setLastLoginDate(new Date());
            userRepository.save(user);
            UserPrinciple userPrinciple = new UserPrinciple(user);
            log.info(String.format(RETURNING_FOUND_USER_BY_USERNAME, username));
            return userPrinciple;
        }
    }

    private void validateNewUser(Users user) {
        if (user.isNotLocked()) {
            if (loginAttemptService.hasExceededMaxAttempts(user.getUsername())) {
                user.setNotLocked(false);
            }else {
                user.setNotLocked(true);
            }
        }else {
            loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
        }
    }

    @Override
    public Users register(String firstname, String lastname, String username, String email) throws EmailNotFoundException, UsernameExistsException, EmailExistsException {
        validateNewUser(StringUtils.EMPTY, username, email);
        Users user = new Users();
        user.setUserId(generateUserId());
        String password = generatePassword();
        String encodedPassword = encodePassword(password);
        user.setFirstname(firstname);
        user.setLastName(lastname);
        user.setUsername(username);
        user.setEmail(email);
        user.setJoinDate(new Date());
        user.setPassword(encodedPassword);
        user.setActive(true);
        user.setNotLocked(true);
        user.setRoles(ROLE_USER.name());
        user.setAuthorities(ROLE_USER.getAuthorities());
        user.setProfileImageUrl(getProfileImageUrl());
        userRepository.save(user);
        log.info("New user password: " + password);
        return user;
    }

    private String getProfileImageUrl() {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(DEFAULT_USER_IMAGE_PATH).toUriString();
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    private String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(10);
    }

    private String generateUserId() {
        return RandomStringUtils.randomNumeric(10);
    }

    private Users validateNewUser(String currentUsername, String newUsername, String newEmail) throws UsernameExistsException, EmailExistsException, EmailNotFoundException {

        Users userByEmail = findUserByEmail(newEmail);
        Users currentUser = findUserByUsername(currentUsername);
        Users userByUsername = findUserByUsername(newUsername);
        if (StringUtils.isNotBlank(currentUsername)) {
            if (userByUsername != null && currentUser.getUserId().equals(userByUsername.getUserId())) {
                throw new UsernameExistsException(USERNAME_ALREADY_EXISTS);
            }
            if (userByEmail != null && currentUser.getUserId().equals(userByEmail.getUserId())) {
                throw new EmailExistsException(EMAIL_ALREADY_EXISTS);
            }
            return currentUser;
        } else {
            if (userByEmail != null) {
                throw new EmailExistsException(EMAIL_ALREADY_EXISTS);
            }
            if (userByUsername != null) {
                throw new UsernameExistsException(USERNAME_ALREADY_EXISTS);
            }
            return null;
        }
    }

    @Override
    public List<Users> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public Users findUserByUsername(String username) {
        return userRepository.findUsersByUsername(username);
    }

    @Override
    public Users findUserByEmail(String email) {
        return userRepository.findUsersByEmail(email);
    }
}