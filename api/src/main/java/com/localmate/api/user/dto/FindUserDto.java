package com.localmate.api.user.dto;

import com.localmate.api.user.domain.Gender;
import com.localmate.api.user.domain.Profile;
import lombok.Getter;

import java.util.List;

@Getter
public class FindUserDto {
    private Long userId;
    private String nickname;
    private Gender gender;
    private List<Long> personalities;
    private int recommendationCount;

    public FindUserDto(Profile profile) {
        this.userId = profile.getUser().getUserId();
        this.nickname = profile.getUser().getNickname();
        this.gender = profile.getUser().getGender();
        this.personalities = profile.getProfilePersonalities().stream()
                .map(pp -> pp.getPersonality().getPersonalityId())
                .toList();
        this.recommendationCount = profile.getRecommendationCount();
    }
}
