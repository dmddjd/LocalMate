package com.localmate.api.user.service;

import com.localmate.api.global.exception.CustomException;
import com.localmate.api.user.domain.Personality;
import com.localmate.api.user.domain.Profile;
import com.localmate.api.user.domain.ProfilePersonality;
import com.localmate.api.user.domain.User;
import com.localmate.api.user.dto.ProfileDto;
import com.localmate.api.user.dto.ProfileUpdateDto;
import com.localmate.api.user.dto.UserUpdateDto;
import com.localmate.api.user.repository.PersonalityRepository;
import com.localmate.api.user.repository.ProfilePersonalityRepository;
import com.localmate.api.user.repository.ProfileRepository;
import com.localmate.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final ProfileRepository profileRepository;
    private final PersonalityRepository personalityRepository;
    private final ProfilePersonalityRepository profilePersonalityRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public ProfileDto getProfile(Long userId) {
        Profile profile = profileRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "프로필이 존재하지 않습니다."));
        return new ProfileDto(profile);
    }

    @Transactional
    public void updateUserInfo(Long userId, UserUpdateDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "유저가 존재하지 않습니다."));

        user.updateInfo(dto.getCountry(), dto.getCity(), dto.getAddressLine1(), dto.getAddressLine2());
    }

    @Transactional
    public void updateProfile(Long userId, ProfileUpdateDto dto) {
        Profile profile = profileRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "프로필이 존재하지 않습니다."));

        User user = profile.getUser();
        if (!user.getNickname().equals(dto.getNickname()) &&
                userRepository.findByNickname(dto.getNickname()).isPresent()) {
            throw new CustomException(HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다.");
        }
        user.updateNickname(dto.getNickname());

        profile.update(dto.getStatusMessage(), dto.isLocalMode());

        profilePersonalityRepository.deleteAllByProfile(profile);

        List<ProfilePersonality> newPersonalities = dto.getPersonalityIds().stream()
                .map(id -> {
                    Personality personality = personalityRepository.findById(id)
                            .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 personality입니다. id: " + id));
                    return new ProfilePersonality(profile, personality);
                })
                .toList();

        profilePersonalityRepository.saveAll(newPersonalities);
    }
}
