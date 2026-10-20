package com.akhilesh.coachingManager.config;

import com.akhilesh.coachingManager.entity.Role;
import com.akhilesh.coachingManager.entity.User;
import com.akhilesh.coachingManager.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OwnerInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${owner.email}")
    private String ownerEmail;

    @Value("${owner.password}")
    private String ownerPassword;

    @PostConstruct
    public void createOwner() {
        if (userRepository.existsByEmail(ownerEmail)) {
            return;
        }

        User owner = new User();
        owner.setEmail(ownerEmail);
        owner.setPassword(passwordEncoder.encode(ownerPassword));
        owner.setRole(Role.OWNER);
        userRepository.save(owner);
    }
}