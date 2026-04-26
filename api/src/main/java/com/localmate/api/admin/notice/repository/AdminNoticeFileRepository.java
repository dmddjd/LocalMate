package com.localmate.api.admin.notice.repository;

import com.localmate.api.admin.notice.domain.AdminNoticeFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminNoticeFileRepository extends JpaRepository<AdminNoticeFile, Long> {
}
