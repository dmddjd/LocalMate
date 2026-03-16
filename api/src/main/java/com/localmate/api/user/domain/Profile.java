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

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "profile")
    private List<Recommendation> recommendations = new ArrayList<>();

    public Profile(User user) {
        this.user = user;
    }

    public void update(String statusMessage, boolean localMode) {
        this.statusMessage = statusMessage;
        this.localMode = localMode;
    }

    public void updateProfileImage(File profileImage) {
        this.profileImage = profileImage;
    }
}
