package com.localmate.api.member.dto;

import com.localmate.api.member.domain.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDto {
    private String memberName;
    private String memberId;
    private String memberPw;
    private String email;
    private LocalDate birthDate;
    private Gender gender;
    private String phoneNumber;
    private String countryCode;
    private String city;
    private String addressLine1;
    private String addressLine2;
}
