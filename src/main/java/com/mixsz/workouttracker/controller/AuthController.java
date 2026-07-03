package com.mixsz.workouttracker.controller;

import com.mixsz.workouttracker.dto.request.LoginRequestDTO;
import com.mixsz.workouttracker.dto.request.RegisterRequestDTO;
import com.mixsz.workouttracker.dto.response.LoginResponseDTO;
import com.mixsz.workouttracker.dto.response.UserResponseDTO;
import com.mixsz.workouttracker.model.User;
import com.mixsz.workouttracker.repository.UserRepository;
import com.mixsz.workouttracker.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import com.mixsz.workouttracker.infra.security.TokenService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

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


    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid LoginRequestDTO data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        var token = tokenService.generateToken((User) auth.getPrincipal());

        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody @Valid RegisterRequestDTO data){
        authService.register(data);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users")
    public ResponseEntity getAllAccounts() {
        var users = this.repository.findAll()
                .stream()
                .map(user -> new UserResponseDTO(user.getId(), user.getName(), user.getEmail(), user.getRole()))
                .toList();
        return ResponseEntity.ok(users);
    }
}
