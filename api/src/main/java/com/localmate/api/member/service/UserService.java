package com.localmate.api.member.service;


import com.localmate.api.exception.CustomException;
import com.localmate.api.member.domain.User;
import com.localmate.api.member.dto.SignupDto;
import com.localmate.api.member.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public Optional<User> findByUserId(String userId) {
        return userRepository.findByUserId(userId);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);
    }

    public void signup(SignupDto signupDto) {
        if(userRepository.findByUserId(signupDto.getUserId()).isPresent()) throw new CustomException("아이디 중복");
        if(userRepository.findByEmail(signupDto.getEmail()).isPresent()) throw new CustomException("이메일 중복");
        if(userRepository.findByPhoneNumber(signupDto.getPhoneNumber()).isPresent()) throw new CustomException("전화번호 중복");

        User user = createUserEntity(signupDto);

        userRepository.save(user);
    }

    private User createUserEntity(SignupDto signupDto) {
        return User.builder()
                .userName(signupDto.getUserName())
                .userId(signupDto.getUserId())
                .userPw(bCryptPasswordEncoder.encode(signupDto.getUserPw()))
                .email(signupDto.getEmail())
                .birthDate(signupDto.getBirthDate())
                .gender(signupDto.getGender())
                .phoneNumber(signupDto.getPhoneNumber())
                .countryCode(signupDto.getCountryCode())
                .city(signupDto.getCity())
                .addressLine1(signupDto.getAddressLine1())
                .addressLine2(signupDto.getAddressLine2())
                .build();
    }

}
