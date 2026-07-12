package com.mixsz.workouttracker.service;

import com.mixsz.workouttracker.dto.request.ExerciseRequestDTO;
import com.mixsz.workouttracker.enums.MuscleGroup;
import com.mixsz.workouttracker.exception.custom.BusinessException;
import com.mixsz.workouttracker.exception.custom.ResourceNotFoundException;
import com.mixsz.workouttracker.model.Exercise;
import com.mixsz.workouttracker.repository.ExerciseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class ExerciseServiceTest {

    @Mock
    ExerciseRepository exerciseRepository;

    @InjectMocks
    ExerciseService exerciseService;

    @Test
    void lancaErroQuandoNaoEncontrarExercicio(){
        UUID id = UUID.randomUUID();
        Mockito.when(exerciseRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> exerciseService.findById(id));
    }

    @Test
    void retornaExercicioQuandoEncontrar(){
        UUID id = UUID.randomUUID();
        Exercise exercise = new Exercise();
        exercise.setId(id);

        Mockito.when(exerciseRepository.findById(id)).thenReturn(Optional.of(exercise));

        Exercise res = exerciseService.findById(id);

        assertEquals(exercise, res);
    }

    @Test
    void retornoPorNomeEMusculo(){
        String name = "Nome";
        MuscleGroup muscleGroup = MuscleGroup.CHEST;
        List<Exercise> exercises = List.of(new Exercise());

        Mockito.when(exerciseRepository.findByNameAndMuscleGroup(name, muscleGroup)).thenReturn(exercises);

        List<Exercise> res = exerciseService.search(name, muscleGroup);

        assertEquals(exercises, res);
    }

    @Test
    void retornoPorNome(){
        String name = "Nome";
        List<Exercise> exercises = List.of(new Exercise());

        Mockito.when(exerciseRepository.findByNameContainingIgnoreCase(name)).thenReturn(exercises);

        List<Exercise> res = exerciseService.search(name, null);


        assertEquals(exercises, res);
    }

    @Test
    void retornoPorExercicio(){
        MuscleGroup muscleGroup = MuscleGroup.CHEST;
        List<Exercise> exercises = List.of(new Exercise());

        Mockito.when(exerciseRepository.findByMuscleGroup(muscleGroup)).thenReturn(exercises);

        List<Exercise> res = exerciseService.search(null, muscleGroup);


        assertEquals(exercises, res);
    }

    @Test
    void retornaTodosExerciciosQuandoNenhumFiltro(){
        List<Exercise> exercises = List.of(new Exercise());

        Mockito.when(exerciseRepository.findAll()).thenReturn(exercises);

        List<Exercise> res = exerciseService.search(null, null);

        assertEquals(exercises, res);
    }

    @Test
    void lancaErroAoTentarCadastrarExercicioJaExistente(){
        Exercise exercise = new Exercise();
        ExerciseRequestDTO exerciseRequestDTO = new ExerciseRequestDTO("Nome", MuscleGroup.CHEST);

        Mockito.when(exerciseRepository.findByName(exerciseRequestDTO.name().trim())).thenReturn(Optional.of(exercise));

        assertThrows(BusinessException.class, () -> exerciseService.save(exerciseRequestDTO));
    }

    @Test
    void retornaExercicioCriado(){
        ExerciseRequestDTO exerciseRequestDTO = new ExerciseRequestDTO("Nome", MuscleGroup.CHEST);
        Exercise exercise = new Exercise();
        exercise.setName(exerciseRequestDTO.name().trim());

        Mockito.when(exerciseRepository.findByName(exerciseRequestDTO.name().trim())).thenReturn(Optional.empty());
        Mockito.when(exerciseRepository.save(Mockito.any(Exercise.class))).thenReturn(exercise);

        Exercise res = exerciseService.save(exerciseRequestDTO);
        assertEquals(exercise, res);
    }
}
