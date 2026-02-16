package com.localmate.api.user.service;


import com.localmate.api.exception.CustomException;
import com.localmate.api.user.domain.User;
import com.localmate.api.user.dto.SignupDto;
import com.localmate.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);
    }

    public void signup(SignupDto signupDto) {
        if(userRepository.findById(signupDto.getId()).isPresent()) throw new CustomException("아이디 중복");
        if(userRepository.findByEmail(signupDto.getEmail()).isPresent()) throw new CustomException("이메일 중복");
        if(userRepository.findByPhoneNumber(signupDto.getPhoneNumber()).isPresent()) throw new CustomException("전화번호 중복");

        User user = createUserEntity(signupDto);

        userRepository.save(user);
    }

    private User createUserEntity(SignupDto signupDto) {
        return User.builder()
                .userName(signupDto.getUserName())
                .id(signupDto.getId())
                .password(passwordEncoder.encode(signupDto.getPassword()))
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
