package com.example.listener;

import com.example.service.LoginAttemptService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Component
@Slf4j
@AllArgsConstructor
public class AuthenticationFailureListener {

    private LoginAttemptService loginAttemptService;

    @EventListener
    public void onAuthenticationFailure(AuthenticationFailureBadCredentialsEvent event) {
        Object principle = event.getAuthentication().getPrincipal();
        if (principle instanceof String) {
            String username = (String) event.getAuthentication().getPrincipal();
            log.info("attempt number is " + loginAttemptService.getAttemptNumber(username));
            loginAttemptService.addUserToLoginAttemptCache(username);
        }
    }

}
