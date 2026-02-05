package com.localmate.api.member.controller;

import com.localmate.api.member.dto.MemberDto;
import com.localmate.api.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Member Controller", description = "멤버 컨트롤러 입니다.")
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "회원가입 API 입니다.")
    public String signup(@RequestBody MemberDto memberDto) {
        return memberService.signup(memberDto);
    }
}
