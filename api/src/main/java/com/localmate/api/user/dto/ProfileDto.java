package com.localmate.api.user.dto;

import com.localmate.api.user.domain.Profile;
import lombok.Getter;

import java.util.List;

@Getter
public class ProfileDto {
    private String profileImageUrl;
    private String nickname;
    private String country;
    private String city;
    private String statusMessage;
    private int recommendationCount;
    private List<Long> personalities;
    private boolean localMode;

    public ProfileDto(Profile profile) {
        this.profileImageUrl = profile.getProfileImagePath();
        this.nickname = profile.getUser().getNickname();
        this.country = profile.getUser().getCountry();
        this.city = profile.getUser().getCity();
        this.statusMessage = profile.getStatusMessage();
        this.localMode = profile.isLocalMode();
        this.personalities = profile.getProfilePersonalities().stream()
                .map(pp -> pp.getPersonality().getPersonalityId())
                .toList();
        this.recommendationCount = profile.getRecommendations().size();
    }
}
