package com.localmate.api.global.file.service;

import com.localmate.api.global.exception.CustomException;
import com.localmate.api.global.file.domain.File;
import com.localmate.api.global.file.domain.FileType;
import com.localmate.api.global.file.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    private final FileRepository fileRepository;

    @Transactional
    public File save(MultipartFile file, String subDir, FileType fileType) {
        String originalName = file.getOriginalFilename();
        String extension = originalName.substring(originalName.lastIndexOf(".") + 1);
        String changeName = UUID.randomUUID() + "." + extension;
        String filePath = "/images/" + subDir + "/" + changeName;

        Path path = Paths.get(uploadDir, subDir, changeName);
        try {
            file.transferTo(path.toFile());
        } catch (IOException e) {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 저장에 실패했습니다.");
        }

        return fileRepository.save(new File(originalName, changeName, extension, file.getSize(), filePath, fileType));
    }

    @Transactional
    public void delete(File file) {
        if (file == null) return;

        Path path = Paths.get(uploadDir, file.getFilePath().replace("/images/", ""));
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 삭제에 실패했습니다.");
        }
        file.delete();
    }
}
