package com.mixsz.workouttracker.service;

import com.mixsz.workouttracker.dto.request.RegisterRequestDTO;
import com.mixsz.workouttracker.enums.UserRole;
import com.mixsz.workouttracker.model.User;
import com.mixsz.workouttracker.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email);
    }

    public void register(RegisterRequestDTO data) {
        if(userRepository.findByEmail(data.email()) != null) {
            throw new RuntimeException("Email já cadastrado!");
        }
        String encryptedPassword = passwordEncoder.encode(data.password());
        User newUser = new User(null, data.name().trim(), data.email().trim(), encryptedPassword, UserRole.USER);
        userRepository.save(newUser);
    }
}
