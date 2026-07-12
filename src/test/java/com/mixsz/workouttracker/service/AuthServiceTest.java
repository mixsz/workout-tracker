package com.mixsz.workouttracker.service;

import com.mixsz.workouttracker.dto.request.RegisterRequestDTO;
import com.mixsz.workouttracker.exception.custom.BusinessException;
import com.mixsz.workouttracker.model.User;
import com.mixsz.workouttracker.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

        @Test
        void lancaExcessaoQuandoEmailNaoForEncontrado(){
            String email = "emailaa@email.com";
            Mockito.when(userRepository.findByEmail(email)).thenReturn(null);
            assertThrows(UsernameNotFoundException.class, () -> authService.loadUserByUsername(email));
        }

    @Test
    void retornarUserQuandoEmailForEncontrado(){
        String email = "emailaa@email.com";
        User user = new User();
        user.setEmail(email);

        Mockito.when(userRepository.findByEmail(email)).thenReturn(user);

        UserDetails result = authService.loadUserByUsername(email);

        assertEquals(user, result);
    }
}
