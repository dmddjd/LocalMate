package com.localmate.api.admin.user.dto;

import com.localmate.api.user.domain.Gender;
import com.localmate.api.user.domain.User;
import lombok.Getter;

@Getter
public class AdminUserListDto {
    private Long userId;
    private String profileImagePath;
    private String nickname;

    public AdminUserListDto(User user) {
        this.userId = user.getUserId();
        this.profileImagePath = user.getProfile().getProfileImagePath();
        this.nickname = user.getNickname();
    }
}
