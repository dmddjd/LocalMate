package com.localmate.api.admin.notice.domain;

import com.localmate.api.global.file.domain.File;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class AdminNoticeFile {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long noticeFileId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id", nullable = false)
    private AdminNotice notice;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id", nullable = false)
    private File file;

    public AdminNoticeFile(AdminNotice notice, File file) {
        this.notice = notice;
        this.file = file;
    }
}
