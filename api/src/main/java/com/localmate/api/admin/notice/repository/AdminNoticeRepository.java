package com.localmate.api.admin.notice.repository;

import com.localmate.api.admin.notice.domain.AdminNotice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AdminNoticeRepository extends JpaRepository<AdminNotice, Long> {
    // 모든 공지 조회
    @Query("select n from AdminNotice n " +
            "order by n.noticeDate desc")
    List<AdminNotice> getAllNotices();

    // 공지 상세 조회
    @Query("select n from AdminNotice n " +
            "left join fetch n.adminNoticeFiles nf " +
            "left join fetch nf.file " +
            "where n.noticeId = :noticeId")
    Optional<AdminNotice> getNoticeDetail(@Param("noticeId") Long noticeId);

    // expired된 경우 -> deleted로 변경
    @Query("select n from AdminNotice n " +
            "left join fetch n.adminNoticeFiles nf " +
            "left join fetch nf.file " +
            "where n.status = 'ACTIVE' " +
            "and n.expireDate is not null " +
            "and n.expireDate < :now")
    List<AdminNotice> findAllExpiredNotices(@Param("now")LocalDateTime now);

    // deleted된 공지 삭제
    @Query("select n from AdminNotice n " +
            "left join fetch n.adminNoticeFiles nf " +
            "left join fetch nf.file " +
            "where n.status = 'DELETED' " +
            "and n.deleteDate < :cutoff")
    List<AdminNotice> findAllDeletedNotices(@Param("cutoff") LocalDateTime cutoff);

}
