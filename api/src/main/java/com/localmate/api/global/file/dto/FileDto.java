package com.localmate.api.global.file.dto;

import com.localmate.api.global.file.domain.File;
import lombok.Getter;

@Getter
public class FileDto {
    private Long fileId;
    private String filePath;

    public FileDto(File file) {
        this.fileId = file.getFileId();
        this.filePath = file.getFilePath();
    }
}
