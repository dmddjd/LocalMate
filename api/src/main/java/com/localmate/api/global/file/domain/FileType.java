package com.localmate.api.global.file.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FileType {
    PROFILE("profile-images"),
    NOTICE("notice-files"),
    CHAT("chat-files");

    private final String subDir;

    public String getFilePath(String changeName) {
        return "/images/" + subDir + "/" + changeName;
    }
}
