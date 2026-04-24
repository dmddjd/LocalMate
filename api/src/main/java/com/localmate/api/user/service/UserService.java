package com.localmate.api.user.service;

import com.localmate.api.global.exception.CustomException;
import com.localmate.api.global.file.domain.File;
import com.localmate.api.global.file.domain.FileType;
import com.localmate.api.global.file.service.FileService;
import com.localmate.api.user.domain.*;
import com.localmate.api.user.dto.FindUserDto;
import com.localmate.api.user.dto.UserSearchDto;
import com.localmate.api.user.dto.ProfileDto;
import com.localmate.api.user.dto.ProfileUpdateDto;
import com.localmate.api.user.dto.UserUpdateDto;
import com.localmate.api.user.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final ProfileRepository profileRepository;
    private final PersonalityRepository personalityRepository;
    private final ProfilePersonalityRepository profilePersonalityRepository;
    private final RecommendationRepository recommendationRepository;
    private final UserRepository userRepository;
    private final FileService fileService;

    @Transactional(readOnly = true)
    public ProfileDto getProfile(Long userId) {
        Profile profile = profileRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "프로필이 존재하지 않습니다."));
        return new ProfileDto(profile);
    }

    @Transactional
    public void updateUserInfo(Long userId, UserUpdateDto dto) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "유저가 존재하지 않습니다."));

        user.updateInfo(dto.getCountry(), dto.getCity(), dto.getAddressLine1(), dto.getAddressLine2());
    }

    @Transactional
    public void updateProfile(Long userId, ProfileUpdateDto dto, MultipartFile profileImage) {
        Profile profile = profileRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "프로필이 존재하지 않습니다."));

        User user = profile.getUser();
        if (dto.getNickname() != null) {
            if (!user.getNickname().equals(dto.getNickname()) &&
                    userRepository.findByNickname(dto.getNickname()).isPresent()) {
                throw new CustomException(HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다.");
            }
            user.updateNickname(dto.getNickname());
        }
        profile.update(dto.getStatusMessage(), dto.isLocalMode());

        if (profileImage != null && !profileImage.isEmpty()) {
            if (profile.getProfileImage() != null) {
                fileService.delete(profile.getProfileImage());
            }
            File newImage = fileService.upload(profileImage, FileType.PROFILE);
            profile.updateProfileImage(newImage);
        }

        profilePersonalityRepository.deleteAllPersonalitiesByProfile(profile);
        if (dto.getPersonalityIds() != null && !dto.getPersonalityIds().isEmpty()) {
            List<Personality> personalities = personalityRepository.findAllById(dto.getPersonalityIds());
            if (personalities.size() != dto.getPersonalityIds().size()) {
                throw new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 personality가 포함되어 있습니다.");
            }
            List<ProfilePersonality> newPersonalities = personalities.stream()
                    .map(personality -> new ProfilePersonality(profile, personality))
                    .toList();
            profilePersonalityRepository.saveAll(newPersonalities);
        }
    }

    @Transactional
    public List<FindUserDto> findUsers(UserSearchDto userSearchDto) {
        return profileRepository.findUsers(
                userSearchDto.getCountry(),
                userSearchDto.getCity(),
                userSearchDto.getGender()
        ).stream().map(FindUserDto::new).toList();
    }

    @Transactional
    public void recommendation(Long fromUserId, Long toUserId) {
        if (fromUserId.equals(toUserId)) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "본인을 추천할 수 없습니다.");
        }

        User toUser = userRepository.findByUserId(toUserId).orElseThrow(
                () -> new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."));

        Optional<Recommendation> existing = recommendationRepository.findByFromUser_UserIdAndToUser_UserId(fromUserId, toUserId);

        if (existing.isPresent()) {
            recommendationRepository.delete(existing.get());
            toUser.getProfile().cancelRecommendation();
        } else {
            User fromUser = userRepository.findByUserId(fromUserId).orElseThrow(
                    () -> new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."));
            recommendationRepository.save(new Recommendation(fromUser, toUser));
            toUser.getProfile().addRecommendation();
        }
    }
}
