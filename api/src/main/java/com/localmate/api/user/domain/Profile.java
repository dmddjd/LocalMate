package com.localmate.api.user.domain;

import com.localmate.api.global.file.domain.File;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id")
    private File profileImage;

    @OneToMany(mappedBy = "profile")
    private List<ProfilePersonality> profilePersonalities = new ArrayList<>();

    @Column(nullable = false, columnDefinition = "int default 0")
    private int recommendationCount = 0;

    @Column(nullable = false)
    private String profileImagePath = "/images/profile-images/default.jpg";

    public Profile(User user) {
        this.user = user;
    }

    public void update(String statusMessage, boolean localMode) {
        if (statusMessage != null) {
            this.statusMessage = statusMessage;
        }
        this.localMode = localMode;
    }

    public void updateProfileImage(File profileImage) {
        this.profileImage = profileImage;
        this.profileImagePath = profileImage.getPath();
    }

    public void addRecommendation() {
        this.recommendationCount++;
    }

    public void cancelRecommendation() {
        this.recommendationCount--;
    }
}
