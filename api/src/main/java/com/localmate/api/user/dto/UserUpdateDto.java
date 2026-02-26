package com.localmate.api.user.dto;

import lombok.Getter;

@Getter
public class UserUpdateDto {
    private String country;
    private String city;
    private String addressLine1;
    private String addressLine2;
}
