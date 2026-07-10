package com.mixsz.workouttracker.controller;

import com.mixsz.workouttracker.dto.request.LoginRequestDTO;
import com.mixsz.workouttracker.dto.request.RefreshTokenRequestDTO;
import com.mixsz.workouttracker.dto.request.RegisterRequestDTO;
import com.mixsz.workouttracker.dto.response.LoginResponseDTO;
import com.mixsz.workouttracker.dto.response.UserResponseDTO;
import com.mixsz.workouttracker.model.RefreshToken;
import com.mixsz.workouttracker.model.User;
import com.mixsz.workouttracker.repository.UserRepository;
import com.mixsz.workouttracker.service.AuthService;
import com.mixsz.workouttracker.service.RefreshTokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import com.mixsz.workouttracker.infra.security.TokenService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository repository;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private AuthService authService;
    @Autowired
    private RefreshTokenService refreshTokenService;


    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDTO>> getAllAccounts() {
        var users = this.repository.findAll()
                .stream()
                .map(user -> new UserResponseDTO(user.getId(), user.getName(), user.getEmail(), user.getRole()))
                .toList();
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getMyAccount() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return ResponseEntity.status(HttpStatus.OK).body(new UserResponseDTO(user.getId(), user.getName(),
                user.getEmail(), user.getRole()));
    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        User user = (User) auth.getPrincipal();

        var token = tokenService.generateToken(user);
        var refreshToken = refreshTokenService.createRefreshToken(user);


        return ResponseEntity.status(HttpStatus.OK).body(new LoginResponseDTO(token, refreshToken.getToken()));
    }


    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@RequestBody @Valid RegisterRequestDTO data) {
        User user = authService.register(data);
        return ResponseEntity.status(HttpStatus.CREATED).body(new UserResponseDTO(user.getId(), user.getName(), user.getEmail(), user.getRole()));
    }


    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDTO> refresh(@RequestBody @Valid RefreshTokenRequestDTO data) {
        RefreshToken refreshToken = refreshTokenService.validateRefreshToken(data.refreshToken());

        User user = refreshToken.getUser();
        var newToken = tokenService.generateToken(user);

        return ResponseEntity.status(HttpStatus.OK).body(new LoginResponseDTO(newToken, refreshToken.getToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody @Valid RefreshTokenRequestDTO data) {
        refreshTokenService.deleteRefreshToken(data.refreshToken());
        return ResponseEntity.noContent().build();
    }
}
