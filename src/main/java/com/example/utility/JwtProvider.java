package com.example.utility;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.constant.SecurityConstant;
import com.example.domain.UserPrinciple;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

@Component
public class JwtProvider {

    @Value("jwt.secret")
    private String secret;

    public String generateJwtToken(UserPrinciple userPrinciple) {
        String[] claims = getClaimsFromUser(userPrinciple);
        return JWT.create().withIssuer(SecurityConstant.MUKATAY_LLC).withAudience(SecurityConstant.MUKATAY_ADMINISTRATION).withIssuedAt(new Date()).withSubject(userPrinciple.getUsername())
                .withArrayClaim(SecurityConstant.AUTHORITIES, claims).withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstant.EXPIRATION_TIME)).sign(Algorithm.HMAC512(secret.getBytes()));
    }

    private String[] getClaimsFromUser(UserPrinciple userPrinciple) {
        List<String> authorities = new ArrayList<>();
        for (GrantedAuthority authority : userPrinciple.getAuthorities()) {
            authorities.add(authority.getAuthority());
        }
        return authorities.toArray(new String[0]);
    }

    public List<GrantedAuthority> getAuthorities(String token) {
        String[] claims = getClaimsFromToken(token);
        return stream(claims).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    private String[] getClaimsFromToken(String token) {
        return getJwtVerifier().verify(token).getClaim(SecurityConstant.AUTHORITIES).asArray(String.class);
    }

    private JWTVerifier getJwtVerifier() {
        JWTVerifier verifier;
        try {
            Algorithm algorithm = Algorithm.HMAC512(secret);
            verifier = JWT.require(algorithm).withIssuer(SecurityConstant.MUKATAY_LLC).build();
        } catch (JWTVerificationException e) {
            throw new JWTVerificationException(SecurityConstant.TOKEN_CANNOT_BE_VERIFIED);
        }
        return verifier;
    }

    public Authentication getAuthentication(String username, List<GrantedAuthority> authorities, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username, null, authorities);
        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return usernamePasswordAuthenticationToken;
    }

    public boolean isTokenValid(String username, String token) {
        return StringUtils.isNotEmpty(username) && !isTokenExpired(getJwtVerifier(), token);
    }

    private boolean isTokenExpired(JWTVerifier verifier, String token) {
        return verifier.verify(token).getExpiresAt().before(new Date());
    }

    public String getSubject(String token) {
        return getJwtVerifier().verify(token).getSubject();
    }
}