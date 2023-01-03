package com.domo.dividend.service;

import com.domo.dividend.model.Auth;
import com.domo.dividend.persist.UserRepository;
import com.domo.dividend.persist.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(
                        () -> new UsernameNotFoundException("couldn't find user -> " + username));
    }

    public UserEntity register(Auth.SignUp user) {
        boolean exists = userRepository.existsByUsername(user.getUsername());
        if (exists) {
            throw new RuntimeException("이미 사용 중인 아이디 입니다");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        var result = userRepository.save(user.toEntity());
        return result;
    }

    public UserEntity authenticate(Auth.SignIn signIn) {
        var user = userRepository.findByUsername(signIn.getUsername())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 ID 입니다."));
        if (!passwordEncoder.matches(signIn.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }
        return user;
    }
}
