package com.localmate.api.admin.user.dto;

import com.localmate.api.user.domain.Gender;
import com.localmate.api.user.domain.Role;
import com.localmate.api.user.domain.UserStatus;
import lombok.Getter;

@Getter
public class AdminUserSearchDto {
    private Role role;
    private Gender gender;
    private UserStatus status;
}
