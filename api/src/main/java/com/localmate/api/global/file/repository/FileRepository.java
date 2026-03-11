package com.localmate.api.global.file.repository;

import com.localmate.api.global.file.domain.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long> {
}
