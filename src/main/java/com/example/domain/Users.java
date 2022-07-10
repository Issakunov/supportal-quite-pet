package com.example.domain;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Users implements Serializable {

    @Id
    @SequenceGenerator(
            sequenceName = "users_seq",
            name = "users_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "user_seq"
    )
    @Column(nullable = false, updatable = false)
    Long id;
    String userId;
    String firstname;
    String lastName;
    String username;
    String password;
    String email;
    String profileImageUrl;
    Date lastLoginDate;
    Date lastLoginDateDisplay;
    Date joinDate;
    String roles;
    String[] authorities;
    boolean isActive;
    boolean isNotLocked;

}
