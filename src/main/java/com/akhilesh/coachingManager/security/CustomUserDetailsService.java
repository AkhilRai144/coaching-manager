package com.akhilesh.coachingManager.security;


import com.akhilesh.coachingManager.entity.User;
import com.akhilesh.coachingManager.repository.StudentRepository;
import com.akhilesh.coachingManager.repository.TeacherRepository;
import com.akhilesh.coachingManager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        boolean removed = studentRepository.existsByUserIdAndDeletedTrue(user.getId())
                || teacherRepository.existsByUserIdAndDeletedTrue(user.getId());

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .disabled(removed)
                .build();
    }
}
