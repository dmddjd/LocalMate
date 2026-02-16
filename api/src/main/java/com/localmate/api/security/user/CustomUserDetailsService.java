package com.localmate.api.security.user;

import com.localmate.api.user.domain.User;
import com.localmate.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return new CustomUserDetails(user.get());
        }

        throw new UsernameNotFoundException("존재하지 않는 아이디 입니다.");
    }
}
