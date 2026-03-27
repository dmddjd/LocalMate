package com.localmate.api.auth.dto;

import com.localmate.api.user.domain.Gender;
import lombok.*;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignupDto {
    private String userName;
    private String nickname;
    private String id;
    private String password;
    private String email;
    private LocalDate birthDate;
    private Gender gender;
    private String phoneNumber;
    private String country;
    private String city;
    private String addressLine1;
    private String addressLine2;
}
