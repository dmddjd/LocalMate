package com.localmate.api.admin.user.dto;

import com.localmate.api.user.domain.Status;
import lombok.Getter;

@Getter
public class AdminUserChangeStatusDto {
    private Status status;
}
