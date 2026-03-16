package com.localmate.api.global.file.service;

import com.localmate.api.global.exception.CustomException;
import com.localmate.api.global.file.domain.File;
import com.localmate.api.global.file.domain.FileType;
import com.localmate.api.global.file.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {
    private final FileRepository fileRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public File upload(MultipartFile multipartFile, FileType fileType) {
        String originalName = multipartFile.getOriginalFilename();
        String extension = getExtension(originalName);
        String changeName = UUID.randomUUID() + "." + extension;

        Path savePath = Paths.get(uploadDir, fileType.getSubDir(), changeName);

        try {
            multipartFile.transferTo(savePath.toFile());
        } catch (IOException e) {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 저장에 실패했습니다.");
        }

        File file = new File(originalName, changeName, fileType);

        return fileRepository.save(file);
    }

    public void delete(File file) {
        file.delete();
    }

    private String getExtension(String originalName) {
        if(originalName == null || !originalName.contains(".")) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "올바르지 않은 파일 이름입니다.");
        }
        return originalName.substring(originalName.lastIndexOf(".") + 1);
    }
}
