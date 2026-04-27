package com.localmate.api.notice.repository;

import com.localmate.api.admin.notice.domain.AdminNotice;
import com.localmate.api.chat.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NoticeRepository extends JpaRepository<AdminNotice, Long> {
    // ACTIVE 공지 조회
    @Query("select n from AdminNotice n " +
            "where n.status = 'ACTIVE'" +
            "order by n.noticeDate desc")
    List<AdminNotice> getNotice();

    // ACTIVE 공지 상세 조회
    @Query("select n from AdminNotice n " +
            "left join fetch n.adminNoticeFiles nf " +
            "left join fetch nf.file " +
            "where n.noticeId = :noticeId " +
            "and n.status = 'ACTIVE'")
    Optional<AdminNotice> getNoticeDetail(@Param("noticeId") Long noticeId);
}
