package com.sebray.jwt;

import com.sebray.jwt.entity.Role;
import com.sebray.jwt.entity.User;
import com.sebray.jwt.exception.ResourceNotFoundException;
import com.sebray.jwt.repository.RoleRepository;
import com.sebray.jwt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class FirstAdminCommandLineRunner implements CommandLineRunner {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (!userRepository.existsByEmail("admin@yandex.ru")) {
            Set<Role> roles = new HashSet<>();
            Role role = roleRepository.findByName("ROLE_ADMIN")
                    .orElseThrow(() -> new ResourceNotFoundException("ROLE_ADMIN does not exist"));
            roles.add(role);
            userRepository.save(new User(
                    null,
                    "admin",
                    "admin@yandex.ru",
                    passwordEncoder.encode("1234"),
                    true,
                    null,
                    roles,
                    null));
        }
    }
}
