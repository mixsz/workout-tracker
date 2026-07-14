package com.mixsz.workouttracker.controller;

import com.mixsz.workouttracker.dto.request.LoginRequestDTO;
import com.mixsz.workouttracker.dto.request.RegisterRequestDTO;
import com.mixsz.workouttracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup(){
        userRepository.deleteAll();
    }

    @Test
    void registrarELogarUsuarioComSucesso() throws Exception {
        RegisterRequestDTO dto = new RegisterRequestDTO(
                "Nomezinho",
                "email@email.com",
                "Senha123!",
                "Senha123!"
        );

        mockMvc.perform(post("/auth/register")
                .contentType("application/json")
                .content(objectMapper.writeValueAsBytes(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());

        LoginRequestDTO dto2 = new LoginRequestDTO(
                "email@email.com",
                "Senha123!"
        );

        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsBytes(dto2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
    }

    @Test
    void erroAoLogarComSenhaErrada() throws Exception {
        RegisterRequestDTO dto = new RegisterRequestDTO(
                "Nomezinho",
                "email@email.com",
                "Senha123!",
                "Senha123!"
        );

        mockMvc.perform(post("/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsBytes(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());

        LoginRequestDTO dto2 = new LoginRequestDTO(
                "email@email.com",
                "sEnha123!"
        );

        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsBytes(dto2)))
                .andExpect(status().isUnauthorized());
    }
}
