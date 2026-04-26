package com.localmate.api.admin.user.dto;

import com.localmate.api.user.domain.Gender;
import com.localmate.api.user.domain.Role;
import com.localmate.api.user.domain.UserStatus;
import com.localmate.api.user.domain.User;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

@Getter
public class AdminUserDetailDto {
    private Long userId;
    private Role role;
    private String profileImagePath;
    private String userName;
    private String nickname;
    private String email;
    private LocalDate birthDate;
    private int age;
    private Gender gender;
    private String phoneNumber;
    private String country;
    private String city;
    private String addressLine1;
    private String addressLine2;
    private LocalDateTime enrollDate;
    private LocalDateTime withdrawDate;
    private UserStatus status;

    public AdminUserDetailDto(User user) {
        this.userId = user.getUserId();
        this.role = user.getRole();
        this.profileImagePath = user.getProfile().getProfileImagePath();
        this.userName = user.getUserName();
        this.nickname = user.getNickname();
        this.email = user.getEmail();
        this.birthDate = user.getBirthDate();
        this.age = Period.between(user.getBirthDate(), LocalDate.now()).getYears();
        this.gender = user.getGender();
        this.phoneNumber = user.getPhoneNumber();
        this.country = user.getCountry();
        this.city = user.getCity();
        this.addressLine1 = user.getAddressLine1();
        this.addressLine2 = user.getAddressLine2();
        this.enrollDate = user.getEnrollDate();
        this.withdrawDate = user.getWithdrawDate();
        this.status = user.getStatus();
    }
}
