package com.localmate.api.mate.dto;

import com.localmate.api.user.domain.Gender;
import lombok.Getter;

@Getter
public class MateSearchDto {
    private String country;
    private String city;
    private Gender gender;
}
