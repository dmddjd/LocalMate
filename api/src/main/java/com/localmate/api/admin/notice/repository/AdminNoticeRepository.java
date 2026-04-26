package com.localmate.api.admin.notice.repository;

import com.localmate.api.admin.notice.domain.AdminNotice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AdminNoticeRepository extends JpaRepository<AdminNotice, Long> {
    // 모든 공지 조회
    @Query("select n from AdminNotice n " +
            "order by n.noticeDate desc")
    List<AdminNotice> getAllNotice();
}
