package com.localmate.api.mate.service;

import com.localmate.api.mate.dto.MateDto;
import com.localmate.api.mate.dto.MateSearchDto;
import com.localmate.api.user.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MateService {
    private final ProfileRepository profileRepository;

    @Transactional
    public List<MateDto> getMates(MateSearchDto mateSearchDto) {
        return profileRepository.findMates(
                mateSearchDto.getCountry(),
                mateSearchDto.getCity(),
                mateSearchDto.getGender()
        ).stream().map(MateDto::new).toList();
    }
}
