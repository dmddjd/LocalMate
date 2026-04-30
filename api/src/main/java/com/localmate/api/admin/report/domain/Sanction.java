package com.localmate.api.admin.report.domain;

import com.localmate.api.user.domain.User;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
public class Sanction {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sanctionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SanctionType sanctionType;

    @Column(nullable = false)
    private LocalDateTime sanctionDate;

    private LocalDateTime suspendedUtil;

    public Sanction(User user, SanctionType sanctionType, LocalDateTime suspendedUtil) {
        this.user = user;
        this.sanctionType = sanctionType;
        this.sanctionDate = LocalDateTime.now();
        this.suspendedUtil = suspendedUtil;
    }
}
