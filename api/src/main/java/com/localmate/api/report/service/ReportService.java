package com.localmate.api.report.service;

import com.localmate.api.global.exception.CustomException;
import com.localmate.api.report.domain.Report;
import com.localmate.api.report.domain.Category;
import com.localmate.api.report.dto.ReportRequestDto;
import com.localmate.api.report.repository.CategoryRepository;
import com.localmate.api.report.repository.ReportCategoryRepository;
import com.localmate.api.report.repository.ReportRepository;
import com.localmate.api.user.domain.User;
import com.localmate.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final CategoryRepository CategoryRepository;
    private final UserRepository userRepository;

    @Transactional
    public void report(Long reporterId, ReportRequestDto dto) {
        if (reportRepository.existsByReporter_UserIdAndReportedIdAndReportType(reporterId, dto.getReportedId(), dto.getReportType())) {
            throw new CustomException(HttpStatus.CONFLICT, "이미 신고한 대상입니다.");
        }

        User reporter = userRepository.findByUserId(reporterId).orElseThrow(
                () -> new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."));

        List<Category> categories = CategoryRepository.findAllById(dto.getCategoryIds());

        if (categories.size() != dto.getCategoryIds().size()) {
            throw new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 신고 카테고리가 포함되어 있습니다.");
        }

        reportRepository.save(new Report(reporter, dto.getReportType(), dto.getReportedId(), categories, dto.getDescription()));
    }
}
