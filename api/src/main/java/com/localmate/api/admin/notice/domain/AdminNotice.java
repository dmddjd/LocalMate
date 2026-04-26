package com.localmate.api.admin.notice.domain;

import com.localmate.api.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class AdminNotice {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long noticeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @OneToMany(mappedBy = "notice")
    private List<AdminNoticeFile> adminNoticeFiles = new ArrayList<>();

    @Column(nullable = false)
    private boolean important = false;

    @Column(nullable = false)
    private LocalDateTime noticeDate;

    private LocalDateTime expireDate;

    private LocalDateTime editDate;

    private LocalDateTime deleteDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NoticeStatus status;

    public AdminNotice(User author, String title, String content, boolean important, LocalDateTime expireDate) {
        this.author = author;
        this.title = title;
        this.content = content;
        this.important = important;
        this.expireDate = expireDate;
    }

    public void editNotice(String title, String content, boolean important, LocalDateTime expireDate) {
        this.title = title;
        this.content = content;
        this.important = important;
        this.expireDate = expireDate;
        this.editDate = LocalDateTime.now();
    }

    public void deleteNotice() {
        this.status = NoticeStatus.DELETE;
        this.deleteDate = LocalDateTime.now();
    }

    @PrePersist
    public void prePersist() {
        this.noticeDate = LocalDateTime.now();
        this.status = NoticeStatus.ACTIVE;
    }
}
