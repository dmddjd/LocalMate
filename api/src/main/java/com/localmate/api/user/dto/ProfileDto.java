package com.localmate.api.user.dto;

import com.localmate.api.user.domain.Profile;
import lombok.Getter;

import java.util.List;

@Getter
public class ProfileDto {
    private final String nickname;
    private final String country;
    private final String city;
    private final String statusMessage;
    private final int recommendationCount;
    private final List<Long> personalities;
    private final boolean localMode;

    public ProfileDto(Profile profile) {
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
