package com.mixsz.workouttracker.service;

import com.mixsz.workouttracker.dto.request.RegisterRequestDTO;
import com.mixsz.workouttracker.exception.custom.BusinessException;
import com.mixsz.workouttracker.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void lancarExcessaoQuandoAsSenhasNaoCoincidem(){
        RegisterRequestDTO registerRequestDTO = new RegisterRequestDTO(
                "Test User", "email@email.com", "senhasS567**", "senhaS123!!");

        assertThrows(BusinessException.class, () -> authService.register(registerRequestDTO));
    }
}
