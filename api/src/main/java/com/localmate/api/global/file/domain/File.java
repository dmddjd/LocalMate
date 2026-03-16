package com.localmate.api.global.file.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "files")
public class File {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fileId;

    @Column(nullable = false)
    private String originalName;

    @Column(nullable = false)
    private String changeName;

    @Column(nullable = false)
    private LocalDateTime uploadDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FileStatus status;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FileType fileType;

    public File(String originalName, String changeName, FileType fileType) {
        this.originalName = originalName;
        this.changeName = changeName;
        this.fileType = fileType;
        this.uploadDate = LocalDateTime.now();
        this.status = FileStatus.ACTIVE;
    }

    public void delete() {
        this.status = FileStatus.DELETED;
    }
}
