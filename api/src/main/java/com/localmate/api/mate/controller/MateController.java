package com.localmate.api.mate.controller;

import com.localmate.api.global.response.ApiResponse;
import com.localmate.api.mate.dto.MateDto;
import com.localmate.api.mate.dto.MateSearchDto;
import com.localmate.api.mate.service.MateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mate")
@Tag(name = "Mate Controller", description = "현지인 목록 조회 API 입니다.")
public class MateController {
    private final MateService mateService;

    @GetMapping("/locals")
    @Operation(summary = "현지인 목록 조회", description = "여행지의 현지인 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<MateDto>>> getMates(MateSearchDto mateSearchDto) {
        return ResponseEntity.ok(ApiResponse.success("현지인 목록 조회 성공!", mateService.getMates(mateSearchDto)));
    }
}