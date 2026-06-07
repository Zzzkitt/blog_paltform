package com.blog.blog.config;

import com.blog.blog.entity.User;
import com.blog.blog.enums.UserRole;
import com.blog.blog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.findByUsername("admin").isPresent()) {
            log.info("管理员用户已存在，跳过初始化");
            return;
        }

        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setNickname("管理员");
        admin.setRole(UserRole.ADMIN);

        userRepository.save(admin);
        log.info("管理员用户创建成功 (username=admin, password=admin123)");
    }
}
