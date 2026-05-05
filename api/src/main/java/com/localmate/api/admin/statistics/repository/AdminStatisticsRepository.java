package com.localmate.api.admin.statistics.repository;

import com.localmate.api.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AdminStatisticsRepository extends JpaRepository<User, Long> {
    // 회원가입 통계
    @Query(value = "select date(u.enroll_date) as period, count(*) as count " +
            "from `user` u " +
            "where u.enroll_date >= :from " +
            "group by period " +
            "order by period asc", nativeQuery = true)
    List<Object[]> countDailySignup(@Param("from")LocalDateTime from);

    @Query(value = "select date_format(date_sub(u.enroll_date, interval weekday(u.enroll_date) day), '%Y-%m-%d') as period, count(*) as count " +
            "from `user` u " +
            "where u.enroll_date >= :from " +
            "group by period " +
            "order by period asc", nativeQuery = true)
    List<Object[]> countWeeklySignup(@Param("from")LocalDateTime from);

    @Query(value = "select date_format(u.enroll_date, '%Y-%m') as period, count(*) as count " +
            "from `user` u " +
            "where u.enroll_date >= :from " +
            "group by period " +
            "order by period asc", nativeQuery = true)
    List<Object[]> countMonthlySignup(@Param("from")LocalDateTime from);

    // 회원탈퇴 통계
    @Query(value = "select date(u.withdraw_date) as period, count(*) as count " +
            "from `user` u " +
            "where u.withdraw_date >= :from " +
            "and u.status = 'DELETE' " +
            "group by period " +
            "order by period asc", nativeQuery = true)
    List<Object[]> countDailyWithdraw(@Param("from") LocalDateTime from);

    @Query(value = "select date_format(date_sub(u.withdraw_date, interval weekday(u.withdraw_date) day), '%Y-%m-%d') as period, count(*) as count " +
            "from `user` u " +
            "where u.withdraw_date >= :from " +
            "and u.status = 'DELETE' " +
            "group by period " +
            "order by period asc", nativeQuery = true)
    List<Object[]> countWeeklyWithdraw(@Param("from") LocalDateTime from);

    @Query(value = "select date_format(u.withdraw_date, '%Y-%m') as period, count(*) as count " +
            "from `user` u " +
            "where u.withdraw_date >= :from " +
            "and u.status = 'DELETE' " +
            "group by period " +
            "order by period asc", nativeQuery = true)
    List<Object[]> countMonthlyWithdraw(@Param("from") LocalDateTime from);

    // 활성 사용자 통계
    @Query("select count(u) " +
            "from User u " +
            "where u.lastLoginDate >= :from " +
            "and u.status = 'ACTIVE'")
    long countActiveUsers(@Param("from") LocalDateTime from);

    // 신고 접수 통계
    @Query(value = "select date(r.report_date) as period, count(*) as count " +
            "from report r " +
            "where r.report_date >= :from " +
            "group by period " +
            "order by period asc", nativeQuery = true)
    List<Object[]> countDailyReport(@Param("from") LocalDateTime from);

    @Query(value = "select date_format(date_sub(r.report_date, interval weekday(r.report_date) day), '%Y-%m-%d') as period, count(*) as count " +
            "from report r " +
            "where r.report_date >= :from " +
            "group by period " +
            "order by period asc", nativeQuery = true)
    List<Object[]> countWeeklyReport(@Param("from") LocalDateTime from);

    @Query(value = "select date_format(r.report_date, '%Y-%m') as period, count(*) as count " +
            "from report r " +
            "where r.report_date >= :from " +
            "group by period " +
            "order by period asc", nativeQuery = true)
    List<Object[]> countMonthlyReport(@Param("from") LocalDateTime from);

    // 제재 처리 통계
    @Query(value = "select date(s.sanction_date) as period, count(*) as count " +
            "from sanction s " +
            "where s.sanction_date >= :from " +
            "group by period " +
            "order by period asc", nativeQuery = true)
    List<Object[]> countDailySanction(@Param("from") LocalDateTime from);

    @Query(value = "select date_format(date_sub(s.sanction_date, interval weekday(s.sanction_date) day), '%Y-%m-%d') as period, count(*) as count " +
            "from sanction s " +
            "where s.sanction_date >= :from " +
            "group by period " +
            "order by period asc", nativeQuery = true)
    List<Object[]> countWeeklySanction(@Param("from") LocalDateTime from);

    @Query(value = "select date_format(s.sanction_date, '%Y-%m') as period, count(*) as count " +
            "from sanction s " +
            "where s.sanction_date >= :from " +
            "group by period " +
            "order by period asc", nativeQuery = true)
    List<Object[]> countMonthlySanction(@Param("from") LocalDateTime from);

    // 채팅방 생성 통계
    @Query(value = "select date(c.create_date) as period, count(*) as count " +
            "from chat_room c " +
            "where c.create_date >= :from " +
            "group by period " +
            "order by period asc", nativeQuery = true)
    List<Object[]> countDailyChatRoom(@Param("from") LocalDateTime from);

    @Query(value = "select date_format(date_sub(c.create_date, interval weekday(c.create_date) day), '%Y-%m-%d') as period, count(*) as count " +
            "from chat_room c " +
            "where c.create_date >= :from " +
            "group by period " +
            "order by period asc", nativeQuery = true)
    List<Object[]> countWeeklyChatRoom(@Param("from") LocalDateTime from);

    @Query(value = "select date_format(c.create_date, '%Y-%m') as period, count(*) as count " +
            "from chat_room c " +
            "where c.create_date >= :from " +
            "group by period " +
            "order by period asc", nativeQuery = true)
    List<Object[]> countMonthlyChatRoom(@Param("from") LocalDateTime from);

    // 채팅 메세지 발송 통계
    @Query(value = "select date(m.send_time) as period, count(*) as count " +
            "from chat_msg m " +
            "where m.send_time >= :from " +
            "and m.status = 'ACTIVE' " +
            "group by period " +
            "order by period asc", nativeQuery = true)
    List<Object[]> countDailyChatMsg(@Param("from") LocalDateTime from);

    @Query(value = "select date_format(date_sub(m.send_time, interval weekday(m.send_time) day), '%Y-%m-%d') as period, count(*) as count " +
            "from chat_msg m " +
            "where m.send_time >= :from " +
            "and m.status = 'ACTIVE' " +
            "group by period " +
            "order by period asc", nativeQuery = true)
    List<Object[]> countWeeklyChatMsg(@Param("from") LocalDateTime from);

    @Query(value = "select date_format(m.send_time, '%Y-%m') as period, count(*) as count " +
            "from chat_msg m " +
            "where m.send_time >= :from " +
            "and m.status = 'ACTIVE' " +
            "group by period " +
            "order by period asc", nativeQuery = true)
    List<Object[]> countMonthlyChatMsg(@Param("from") LocalDateTime from);
}
