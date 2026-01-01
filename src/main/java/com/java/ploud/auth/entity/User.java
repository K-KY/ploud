package com.java.ploud.auth.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userSeq;
    private String userName;
    @Column(unique = true)
    private String userEmail;
    private String password;

    public void changePassword(String newPassword) {
        this.password = newPassword;
    }

    public void changeUserName(String userName) {
        this.userName = userName;
    }
}
