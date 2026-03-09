package com.localmate.api.mate.dto;

import com.localmate.api.user.domain.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MateSearchDto {
    private String country;
    private String city;
    private Gender gender;
}
