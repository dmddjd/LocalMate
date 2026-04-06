package com.localmate.api.global.file.repository;

import com.localmate.api.global.file.domain.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface FileRepository extends JpaRepository<File, Long> {
    @Query("select f from File f " +
            "where f.status = 'deleted' " +
            "and f.deletedDate < :cutoff")
    List<File> findAllDeletedFile(@Param("cutoff") LocalDateTime cutoff);
}
