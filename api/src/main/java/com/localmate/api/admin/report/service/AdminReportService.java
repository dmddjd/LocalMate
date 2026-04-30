package com.localmate.api.admin.report.service;

import com.localmate.api.admin.report.domain.Sanction;
import com.localmate.api.admin.report.domain.SanctionType;
import com.localmate.api.admin.report.dto.AdminReportDetailDto;
import com.localmate.api.admin.report.dto.AdminReportListDto;
import com.localmate.api.admin.report.repository.AdminReportRepository;
import com.localmate.api.admin.report.repository.SanctionRepository;
import com.localmate.api.global.exception.CustomException;
import com.localmate.api.global.redis.RedisUtil;
import com.localmate.api.report.domain.Report;
import com.localmate.api.report.domain.ReportStatus;
import com.localmate.api.user.domain.User;
import com.localmate.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminReportService {
    private final AdminReportRepository adminReportRepository;
    private final UserRepository userRepository;
    private final SanctionRepository sanctionRepository;
    private final RedisUtil redisUtil;

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

    @Transactional
    public void reportSanction(Long reportId) {
        Report report = adminReportRepository.getReportDetail(reportId).orElseThrow(
                () -> new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 신고입니다."));

        ReportStatus previousStatus = report.getStatus();
        report.reportSanction();

        User reportedUser = userRepository.findByUserId(report.getReportedId()).orElseThrow(
                () -> new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."));

        if (previousStatus != ReportStatus.SANCTIONED) {
            reportedUser.incrementReportCount();
        }

        applySanction(reportedUser);
    }

    @Transactional
    public void reportReject(Long reportId) {
        Report report = adminReportRepository.getReportDetail(reportId).orElseThrow(
                () -> new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 신고입니다."));

        ReportStatus previousStatus = report.getStatus();
        report.reportReject();

        if (previousStatus == ReportStatus.SANCTIONED) {
            User reportedUser = userRepository.findByUserId(report.getReportedId()).orElseThrow(
                    () -> new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."));
                    reportedUser.decrementReportCount();

                    reportedUser.liftSuspension();
                    redisUtil.deleteData("Suspend:" + reportedUser.getUserId());
        }
    }

    private void applySanction(User user) {
        SanctionType sanctionType = determineSanctionType(user.getReportCount());
        LocalDateTime suspendedUtil = calculateSuspendedUtil(sanctionType);

        if (suspendedUtil != null) {
            user.suspend(suspendedUtil);
            redisUtil.deleteData(user.getUserId().toString()); // refresh token 무효화
            redisUtil.setDataExpire(
                    "Suspend:" + user.getUserId(),
                    sanctionType.getMessage(),
                    Duration.between(LocalDateTime.now(),suspendedUtil).getSeconds()
            );
        }

        sanctionRepository.save(new Sanction(user, sanctionType, suspendedUtil));
        log.info("제재 적용 - userId: {}, type: {}, until: {}", user.getUserId(), sanctionType, suspendedUtil);
    }

    private SanctionType determineSanctionType(int reportCount) {
        if (reportCount >= 10) return SanctionType.SANCTION_4;
        if (reportCount >= 7) return SanctionType.SANCTION_3;
        if (reportCount >= 5) return SanctionType.SANCTION_2;
        if (reportCount >= 3) return SanctionType.SANCTION_1;
        return SanctionType.WARNING;
    }

    private LocalDateTime calculateSuspendedUtil(SanctionType type) {
        return switch (type) {
            case WARNING -> null;
            case SANCTION_1 -> LocalDateTime.now().plusDays(1);
            case SANCTION_2 -> LocalDateTime.now().plusDays(3);
            case SANCTION_3 -> LocalDateTime.now().plusDays(5);
            case SANCTION_4 -> LocalDateTime.now().plusDays(7);
            case SANCTION_5 -> LocalDateTime.now().plusDays(9);
        };

    }
}
