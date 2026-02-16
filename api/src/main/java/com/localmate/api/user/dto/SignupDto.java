package com.localmate.api.user.dto;

import com.localmate.api.user.domain.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignupDto {
    private String userName;
    private String id;
    private String password;
    private String email;
    private LocalDate birthDate;
    private Gender gender;
    private String phoneNumber;
    private String countryCode;
    private String city;
    private String addressLine1;
    private String addressLine2;
}
