package com.localmate.api.admin.user.service;

import com.localmate.api.admin.user.dto.AdminUserDetailDto;
import com.localmate.api.admin.user.dto.AdminUserListDto;
import com.localmate.api.admin.user.repository.AdminUserRepository;
import com.localmate.api.global.exception.CustomException;
import com.localmate.api.user.domain.Role;
import com.localmate.api.user.domain.Status;
import com.localmate.api.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminUserService {
    private final AdminUserRepository adminUserRepository;

    @Transactional(readOnly = true)
    public List<AdminUserListDto> getAllUser() {
        return adminUserRepository.getAllUser().stream().map(AdminUserListDto::new).toList();
    }

    @Transactional(readOnly = true)
    public AdminUserDetailDto getUserDtail(Long userId) {
        User user = adminUserRepository.getUserDetail(userId).orElseThrow(
                () -> new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."));
        return new AdminUserDetailDto(user);
    }

    @Transactional
    public void changeRole(Long userId, Role role) {
        User user = adminUserRepository.findById(userId).orElseThrow(
                () -> new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."));
        user.changeRole(role);
    }

    @Transactional
    public void changeStatus(Long userId, Status status) {
        User user = adminUserRepository.findById(userId).orElseThrow(
                () -> new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."));
        user.changeStatus(status);
    }
}
