package com.example.resource;

import com.example.domain.UserPrinciple;
import com.example.domain.Users;
import com.example.exception.EmailExistsException;
import com.example.exception.EmailNotFoundException;
import com.example.exception.UsernameExistsException;
import com.example.exception.UsernameNotFoundException;
import com.example.exception.domain.ExceptionHandling;
import com.example.service.UserService;
import com.example.utility.JwtProvider;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import static com.example.constant.SecurityConstant.JWT_HEADER;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(path = {"/", "/api/v1/user"})
@AllArgsConstructor
public class UserResource extends ExceptionHandling {

    private AuthenticationManager authenticationManager;
    private JwtProvider jwtProvider;

    private final UserService userService;

    @GetMapping("/test")
    public String methodForTest() throws EmailExistsException {
        throw new EmailExistsException("This email is already taken!");
    }
    @GetMapping("/")
    public String methodForCheck() throws UsernameNotFoundException {
        throw new UsernameNotFoundException("User not found by username: " + "Ata");
    }
    @PostMapping("/register")
    public ResponseEntity<Users> register(@RequestBody Users user) throws EmailNotFoundException, UsernameExistsException, EmailExistsException {
        Users user1 = userService.register(user.getFirstname(), user.getLastName(), user.getUsername(), user.getEmail());
        return new ResponseEntity<>(user1, OK);
    }
    @PostMapping("/login")
    public ResponseEntity<Users> login(@RequestBody Users user) {
        authenticate(user.getUsername(), user.getPassword());
        Users loginUser = userService.findUserByUsername(user.getUsername());
        UserPrinciple userPrinciple = new UserPrinciple(loginUser);
        HttpHeaders jwtHeader = getJwtHeader(userPrinciple);
        return new ResponseEntity<>(loginUser, jwtHeader, OK);
    }

    private HttpHeaders getJwtHeader(UserPrinciple userPrinciple) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(JWT_HEADER, jwtProvider.generateJwtToken(userPrinciple));
        return headers;
    }

    private void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

}
