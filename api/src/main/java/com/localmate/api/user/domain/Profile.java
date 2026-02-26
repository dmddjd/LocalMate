package com.localmate.api.user.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Profile {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long profileId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private String statusMessage;

    @Column(nullable = false)
    private boolean localMode = false;

//    private String profileImageUrl;

    @OneToMany(mappedBy = "profile")
    private List<ProfilePersonality> profilePersonalities = new ArrayList<>();

    @OneToMany(mappedBy = "profile")
    private List<Recommendation> recommendations = new ArrayList<>();

    public Profile(User user) {
        this.user = user;
    }

    public void update(String statusMessage, boolean localMode) {
        this.statusMessage = statusMessage;
        this.localMode = localMode;
    }
}
