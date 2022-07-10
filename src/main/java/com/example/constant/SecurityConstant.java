package com.example.constant;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(makeFinal = true)
public class SecurityConstant {

    public static long EXPIRATION_TIME = 432_000_000;
    public static String TOKEN_PREFIX = "Bearer ";
    public static String JWT_HEADER = "Jwt-Token-Ata";
    public static String TOKEN_CANNOT_BE_VERIFIED = "Token cannot be verified";
    public static String MUKATAY_LLC = "MUKATAY, LLC";
    public static String MUKATAY_ADMINISTRATION = "User management portal";
    public static String AUTHORITIES = "authorities_I_created";
    public static String FORBIDDEN_MESSAGE = "You need to log in to access this page";
    public static String ACCESS_DENIED_MESSAGE = "You do not have permission to access this page";
    public static String OPTIONS_HTTP_METHOD = "OPTIONS";
    public static String [] PUBLIC_URLS = {"/**"};
//    public static String [] PUBLIC_URLS = {"/user/login", "user/register", "user/resetpassword/**", "user/image/**"};

}
