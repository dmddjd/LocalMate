package com.localmate.api.member.service;


import com.localmate.api.exception.CustomException;
import com.localmate.api.member.domain.Member;
import com.localmate.api.member.dto.MemberDto;
import com.localmate.api.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public Optional<Member> findByMemberInfo(String memberId) {
        return memberRepository.findByMemberId(memberId);
    }

    public Optional<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    public Optional<Member> findByPhoneNumber(String phoneNumber) {
        return memberRepository.findByPhoneNumber(phoneNumber);
    }

    public String signup(MemberDto memberDto) {
        if(memberRepository.findByMemberId(memberDto.getMemberId()).isPresent()) throw new CustomException("아이디 중복");
        if(memberRepository.findByEmail(memberDto.getEmail()).isPresent()) throw new CustomException("이메일 중복");
        if(memberRepository.findByPhoneNumber(memberDto.getPhoneNumber()).isPresent()) throw new CustomException("전화번호 중복");

        Member member = Member.builder()
                .memberName(memberDto.getMemberName())
                .memberId(memberDto.getMemberId())
                .memberPw(memberDto.getMemberPw())
                .email(memberDto.getEmail())
                .birthDate(memberDto.getBirthDate())
                .gender(memberDto.getGender())
                .phoneNumber(memberDto.getPhoneNumber())
                .countryCode(memberDto.getCountryCode())
                .city(memberDto.getCity())
                .addressLine1(memberDto.getAddressLine1())
                .addressLine2(memberDto.getAddressLine2())
                .build();

        memberRepository.save(member);
        return "회원가입 성공";
    }
}
