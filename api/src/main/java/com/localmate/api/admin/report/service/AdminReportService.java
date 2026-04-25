package com.localmate.api.admin.report.service;

import com.localmate.api.admin.report.dto.AdminReportDetailDto;
import com.localmate.api.admin.report.dto.AdminReportListDto;
import com.localmate.api.admin.report.repository.AdminReportRepository;
import com.localmate.api.global.exception.CustomException;
import com.localmate.api.report.domain.Report;
import com.localmate.api.user.domain.User;
import com.localmate.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminReportService {
    private final AdminReportRepository adminReportRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<AdminReportListDto> getAllReport() {
        return adminReportRepository.getAllReport().stream().map(AdminReportListDto::new).toList();
    }

    @Transactional(readOnly = true)
    public AdminReportDetailDto getReportDetail(Long reportId) {
        Report report = adminReportRepository.getReportDetail(reportId).orElseThrow(
                () -> new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 신고입니다."));

        User reportedUser = userRepository.findByUserId(report.getReportedId()).orElseThrow(
                () -> new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."));

        return new AdminReportDetailDto(report, reportedUser);
    }
}
