package com.mixsz.workouttracker.service;

import com.mixsz.workouttracker.exception.custom.BusinessException;
import com.mixsz.workouttracker.exception.custom.ResourceNotFoundException;
import com.mixsz.workouttracker.model.RefreshToken;
import com.mixsz.workouttracker.repository.RefreshTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class RefreshTokenServiceTest {

    @Mock
    RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    RefreshTokenService refreshTokenService;

    @Test
    void lancaErroQuandoNaoEncontraToken(){
        String token = "aolaolaol";

        Mockito.when(refreshTokenRepository.findByToken(token)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> refreshTokenService.validateRefreshToken(token));
    }

    @Test
    void lancaErroQuandoTokenExpirado(){
        String token = "aolaola";
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setExpiresAt(LocalDateTime.now().minusDays(1));

        Mockito.when(refreshTokenRepository.findByToken(token)).thenReturn(Optional.of(refreshToken));


        assertThrows(BusinessException.class, () -> refreshTokenService.validateRefreshToken(token));
    }

    @Test
    void retornaRefreshToken(){
        String token = "aolaola";
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setExpiresAt(LocalDateTime.now().plusDays(1));

        Mockito.when(refreshTokenRepository.findByToken(token)).thenReturn(Optional.of(refreshToken));


        RefreshToken res = refreshTokenService.validateRefreshToken(token);

        assertEquals(refreshToken, res);
    }

}
