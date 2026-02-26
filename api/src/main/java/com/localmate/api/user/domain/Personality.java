package com.localmate.api.user.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Personality {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long personalityId;

    @Column(nullable = false, unique = true)
    private String personalityName;
}
