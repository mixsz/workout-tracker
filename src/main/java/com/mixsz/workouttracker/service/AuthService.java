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
import com.mixsz.workouttracker.exception.custom.BusinessException;

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
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("Usuário não encontrado com o email: " + email);
        }
        return user;
    }

    public User register(RegisterRequestDTO data) {
        if (!data.password().equals(data.confirmPassword())) {
            throw new BusinessException("As senhas não coincidem!");
        }
        if(userRepository.findByEmail(data.email()) != null) {
            throw new BusinessException("Email já cadastrado!");
        }
        String encryptedPassword = passwordEncoder.encode(data.password());
        User newUser = new User(null, data.name().trim(), data.email().trim(), encryptedPassword, UserRole.USER);
        return userRepository.save(newUser);
    }
}
