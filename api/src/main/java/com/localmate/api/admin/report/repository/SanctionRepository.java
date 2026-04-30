package com.localmate.api.admin.report.repository;

import com.localmate.api.admin.report.domain.Sanction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SanctionRepository extends JpaRepository<Sanction, Long> {
}
