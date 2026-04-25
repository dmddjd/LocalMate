package com.localmate.api.admin.user.dto;

import com.localmate.api.user.domain.Role;
import lombok.Getter;

@Getter
public class AdminUserChangeRoleDto {
    private Role role;
}
