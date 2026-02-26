package com.localmate.api.user.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false, unique = true)
    private String id;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private LocalDate birthDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String addressLine1;

    @Column(nullable = false)
    private String addressLine2;

    @Column(nullable = false)
    private LocalDateTime enrollDate;

    private LocalDateTime withdrawDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private Profile profile;

    @Builder
    public User(String userName, String nickname, String id, String password, String email, LocalDate birthDate, Gender gender, String phoneNumber, String country, String city, String addressLine1, String addressLine2) {
        this.role = Role.USER;
        this.userName = userName;
        this.nickname = nickname;
        this.id = id;
        this.password = password;
        this.email = email;
        this.birthDate = birthDate;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.country = country;
        this.city = city;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.enrollDate = LocalDateTime.now();
        this.status = Status.ACTIVE;
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateInfo(String country, String city, String addressLine1, String addressLine2) {
        this.country = country;
        this.city = city;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
    }
}
