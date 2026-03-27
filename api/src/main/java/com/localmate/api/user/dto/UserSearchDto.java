package com.localmate.api.user.dto;

import com.localmate.api.user.domain.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserSearchDto {
    private String country;
    private String city;
    private Gender gender;
}
