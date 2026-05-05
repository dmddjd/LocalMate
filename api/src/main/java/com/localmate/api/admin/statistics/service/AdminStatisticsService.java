package com.localmate.api.admin.statistics.service;

import com.localmate.api.admin.statistics.dto.StatisticsDto;
import com.localmate.api.admin.statistics.repository.AdminStatisticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminStatisticsService {
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter MONTH_FMT = DateTimeFormatter.ofPattern("yyyy-MM");
    private final AdminStatisticsRepository statisticsRepository;

    public List<StatisticsDto> getSignupStats(String unit) {
        return switch (unit) {
            case "day" -> fillDailyGaps(toDto(statisticsRepository.countDailySignup(
                    LocalDate.now().minusDays(29).atStartOfDay())));
            case "week" -> fillWeeklyGaps(toDto(statisticsRepository.countWeeklySignup(
                    LocalDate.now().with(DayOfWeek.MONDAY).minusWeeks(11).atStartOfDay())));
            case "month" -> fillMonthlyGaps(toDto(statisticsRepository.countMonthlySignup(
                    LocalDate.now().minusMonths(11).withDayOfMonth(1).atStartOfDay())));
            default -> throw new IllegalArgumentException("unit은 day, week, month 중 하나여야 합니다.");
        };
    }

    public List<StatisticsDto> getWithdrawStats(String unit) {
        return switch (unit) {
            case "day" -> fillDailyGaps(toDto(statisticsRepository.countDailyWithdraw(
                    LocalDate.now().minusDays(29).atStartOfDay())));
            case "week" -> fillWeeklyGaps(toDto(statisticsRepository.countWeeklyWithdraw(
                    LocalDate.now().with(DayOfWeek.MONDAY).minusWeeks(11).atStartOfDay())));
            case "month" -> fillMonthlyGaps(toDto(statisticsRepository.countMonthlyWithdraw(
                    LocalDate.now().minusMonths(11).withDayOfMonth(1).atStartOfDay())));
            default -> throw new IllegalArgumentException("unit은 day, week, month 중 하나여야 합니다.");
        };
    }

    public long getActiveUserStats(String unit) {
        return switch (unit) {
            case "dau" -> statisticsRepository.countActiveUsers(LocalDate.now().atStartOfDay());
            case "wau" -> statisticsRepository.countActiveUsers(LocalDate.now().minusDays(6).atStartOfDay());
            case "mau" -> statisticsRepository.countActiveUsers(LocalDate.now().minusDays(29).atStartOfDay());
            default -> throw new IllegalArgumentException("unit은 dau, wau, mau 중 하나여야 합니다.");
        };
    }

    public List<StatisticsDto> getReportStats(String unit) {
        return switch (unit) {
            case "day" -> fillDailyGaps(toDto(statisticsRepository.countDailyReport(
                    LocalDate.now().minusDays(29).atStartOfDay())));
            case "week" -> fillWeeklyGaps(toDto(statisticsRepository.countWeeklyReport(
                    LocalDate.now().with(DayOfWeek.MONDAY).minusWeeks(11).atStartOfDay())));
            case "month" -> fillMonthlyGaps(toDto(statisticsRepository.countMonthlyReport(
                    LocalDate.now().minusMonths(11).withDayOfMonth(1).atStartOfDay())));
            default -> throw new IllegalArgumentException("unit은 day, week, month 중 하나여야 합니다.");
        };
    }

    public List<StatisticsDto> getSanctionStats(String unit) {
        return switch (unit) {
            case "day" -> fillDailyGaps(toDto(statisticsRepository.countDailySanction(
                    LocalDate.now().minusDays(29).atStartOfDay())));
            case "week" -> fillWeeklyGaps(toDto(statisticsRepository.countWeeklySanction(
                    LocalDate.now().with(DayOfWeek.MONDAY).minusWeeks(11).atStartOfDay())));
            case "month" -> fillMonthlyGaps(toDto(statisticsRepository.countMonthlySanction(
                    LocalDate.now().minusMonths(11).withDayOfMonth(1).atStartOfDay())));
            default -> throw new IllegalArgumentException("unit은 day, week, month 중 하나여야 합니다.");
        };
    }

    public List<StatisticsDto> getChatRoomStats(String unit) {
        return switch (unit) {
            case "day" -> fillDailyGaps(toDto(statisticsRepository.countDailyChatRoom(
                    LocalDate.now().minusDays(29).atStartOfDay())));
            case "week" -> fillWeeklyGaps(toDto(statisticsRepository.countWeeklyChatRoom(
                    LocalDate.now().with(DayOfWeek.MONDAY).minusWeeks(11).atStartOfDay())));
            case "month" -> fillMonthlyGaps(toDto(statisticsRepository.countMonthlyChatRoom(
                    LocalDate.now().minusMonths(11).withDayOfMonth(1).atStartOfDay())));
            default -> throw new IllegalArgumentException("unit은 day, week, month 중 하나여야 합니다.");
        };
    }

    public List<StatisticsDto> getChatMsgStats(String unit) {
        return switch (unit) {
            case "day" -> fillDailyGaps(toDto(statisticsRepository.countDailyChatMsg(
                    LocalDate.now().minusDays(29).atStartOfDay())));
            case "week" -> fillWeeklyGaps(toDto(statisticsRepository.countWeeklyChatMsg(
                    LocalDate.now().with(DayOfWeek.MONDAY).minusWeeks(11).atStartOfDay())));
            case "month" -> fillMonthlyGaps(toDto(statisticsRepository.countMonthlyChatMsg(
                    LocalDate.now().minusMonths(11).withDayOfMonth(1).atStartOfDay())));
            default -> throw new IllegalArgumentException("unit은 day, week, month 중 하나여야 합니다.");
        };
    }

    private List<StatisticsDto> fillDailyGaps(List<StatisticsDto> data) {
        Map<String, Long> map = toMap(data);
        return LocalDate.now().minusDays(29)
                .datesUntil(LocalDate.now().plusDays(1))
                .map(date -> new StatisticsDto(date.format(DATE_FMT),map.getOrDefault(date.format(DATE_FMT), 0L)))
                .toList();
    }

    private List<StatisticsDto> fillWeeklyGaps(List<StatisticsDto> data) {
        Map<String, Long> map = toMap(data);
        LocalDate monday = LocalDate.now().with(DayOfWeek.MONDAY).minusWeeks(11);
        List<StatisticsDto> result = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            String period = monday.format(DATE_FMT);
            result.add(new StatisticsDto(period, map.getOrDefault(period, 0L)));
            monday = monday.plusWeeks(1);
        }
        return result;
    }

    private List<StatisticsDto> fillMonthlyGaps(List<StatisticsDto> data) {
        Map<String, Long> map = toMap(data);
        YearMonth start = YearMonth.now().minusMonths(11);
        List<StatisticsDto> result = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            String period = start.plusMonths(i).format(MONTH_FMT);
            result.add(new StatisticsDto(period, map.getOrDefault(period, 0L)));
        }
        return result;
    }

    private Map<String, Long> toMap(List<StatisticsDto> data) {
        return data.stream().collect(Collectors.toMap(StatisticsDto::getPeriod, StatisticsDto::getCount));
    }

    private List<StatisticsDto> toDto(List<Object[]> rows) {
        return rows.stream().map(
                row -> new StatisticsDto(row[0].toString(), ((Number) row[1]).longValue())).toList();
    }
}
