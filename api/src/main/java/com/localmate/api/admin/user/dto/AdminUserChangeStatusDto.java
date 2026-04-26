package com.localmate.api.admin.user.dto;

import com.localmate.api.user.domain.UserStatus;
import lombok.Getter;

@Getter
public class AdminUserChangeStatusDto {
    private UserStatus userStatus;
}
