package com.mixsz.workouttracker.controller;

import com.mixsz.workouttracker.dto.request.LoginRequestDTO;
import com.mixsz.workouttracker.dto.request.RegisterRequestDTO;
import com.mixsz.workouttracker.dto.request.WorkoutRequestDTO;
import com.mixsz.workouttracker.dto.response.LoginResponseDTO;
import com.mixsz.workouttracker.repository.UserRepository;
import com.mixsz.workouttracker.repository.WorkoutRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class WorkoutControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WorkoutRepository workoutRepository;

    private String token;

    @BeforeEach
    void setup() throws Exception{
        userRepository.deleteAll();
        workoutRepository.deleteAll();

        RegisterRequestDTO registerDto = new RegisterRequestDTO(
                "Nomezinho", "email@email.com", "Senha123!", "Senha123!"
        );
        mockMvc.perform(post("/auth/register")
                .contentType("application/json")
                .content(objectMapper.writeValueAsBytes(registerDto)));

        LoginRequestDTO loginDto = new LoginRequestDTO("email@email.com", "Senha123!");
        String response = mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsBytes(loginDto)))
                        .andReturn().getResponse().getContentAsString();

        token = objectMapper.readValue(response, LoginResponseDTO.class).token();
    }

    @Test
    void criaTreinoComSucesso() throws Exception{
        WorkoutRequestDTO dto = new WorkoutRequestDTO("Treino Teste");

        mockMvc.perform(post("/workout")
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")
                .content(objectMapper.writeValueAsBytes(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Treino Teste"))
                .andExpect(jsonPath("$.position").value(0));

        assertEquals(1, workoutRepository.findAll().size());
    }
}
