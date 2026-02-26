package com.localmate.api.user.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class ProfileUpdateDto {
    private String nickname;
    private String statusMessage;
    private boolean localMode;
    private List<Long> personalityIds;
}
