package com.localmate.api.auth.dto;

import lombok.Data;

@Data
public class LoginRequestDto {
    private String id;
    private String password;
}
