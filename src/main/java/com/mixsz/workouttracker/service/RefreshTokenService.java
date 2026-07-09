    package com.mixsz.workouttracker.service;

    import com.mixsz.workouttracker.exception.custom.BusinessException;
    import com.mixsz.workouttracker.exception.custom.ResourceNotFoundException;
    import com.mixsz.workouttracker.model.RefreshToken;
    import com.mixsz.workouttracker.model.User;
    import com.mixsz.workouttracker.repository.RefreshTokenRepository;
    import org.springframework.stereotype.Service;

    import java.time.LocalDateTime;
    import java.util.UUID;

    @Service
    public class RefreshTokenService {

        private final RefreshTokenRepository refreshTokenRepository;

        public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
            this.refreshTokenRepository = refreshTokenRepository;
        }

        public RefreshToken createRefreshToken(User user){
            RefreshToken refreshToken = new RefreshToken();
            refreshToken.setUser(user);
            refreshToken.setToken(UUID.randomUUID().toString());
            refreshToken.setCreatedAt(LocalDateTime.now());
            refreshToken.setExpiresAt(LocalDateTime.now().plusDays(7)); // vou deixar por 7 dias...
            return refreshTokenRepository.save(refreshToken);
        }

        public RefreshToken validateRefreshToken(String token){
            RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                    .orElseThrow(() -> new ResourceNotFoundException("Refresh token não encontrado!"));
            if(refreshToken.getExpiresAt().isBefore(LocalDateTime.now())){
                refreshTokenRepository.delete(refreshToken);
                throw new BusinessException("Refresh token expirado!");
            }
            return refreshToken;
        }

        public void deleteRefreshToken(String token){
            RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                    .orElseThrow(() -> new ResourceNotFoundException("Refresh token não encontrado!"));
            refreshTokenRepository.delete(refreshToken);
        }

    }
