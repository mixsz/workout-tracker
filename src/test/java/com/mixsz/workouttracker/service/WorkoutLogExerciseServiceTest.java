package com.mixsz.workouttracker.service;

import com.mixsz.workouttracker.dto.request.WorkoutLogExerciseRequestDTO;
import com.mixsz.workouttracker.exception.custom.BusinessException;
import com.mixsz.workouttracker.exception.custom.ResourceNotFoundException;
import com.mixsz.workouttracker.model.*;
import com.mixsz.workouttracker.repository.ExerciseRepository;
import com.mixsz.workouttracker.repository.WorkoutExerciseRepository;
import com.mixsz.workouttracker.repository.WorkoutLogExerciseRepository;
import com.mixsz.workouttracker.repository.WorkoutLogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class WorkoutLogExerciseServiceTest {

    @Mock
    WorkoutLogExerciseRepository workoutLogExerciseRepository;

    @Mock
    WorkoutLogRepository workoutLogRepository;

    @Mock
    ExerciseRepository exerciseRepository;

    @Mock
    WorkoutExerciseRepository workoutExerciseRepository;

    @InjectMocks
    WorkoutLogExerciseService workoutLogExerciseService;

    @Test
    void lancaErroQuandoNaoEncontraRegistroDeTreino(){
        UUID workoutLogId = UUID.randomUUID();
        User user = new User();

        Mockito.when(workoutLogRepository.findByIdAndUser(workoutLogId, user)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->  workoutLogExerciseService.findAll(workoutLogId, user));
    }

    @Test
    void lancaErroQuandoNaoEncontraExercicioNoRegistroTreino(){
        UUID workoutLogId = UUID.randomUUID();
        UUID exerciseId = UUID.randomUUID();
        User user = new User();

        Mockito.when(workoutLogRepository.findByIdAndUser(workoutLogId, user)).thenReturn(Optional.of(new WorkoutLog()));
        Mockito.when(workoutLogExerciseRepository.findByWorkoutLogIdAndExerciseId(workoutLogId, exerciseId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->  workoutLogExerciseService.findById(workoutLogId, exerciseId, user));
    }

    @Test
    void lancaErroQuandoExercicioNaoPertenceAoTreino(){
        UUID workoutLogId = UUID.randomUUID();
        UUID workoutId = UUID.randomUUID();
        UUID exerciseId = UUID.randomUUID();
        WorkoutLogExerciseRequestDTO dto = new WorkoutLogExerciseRequestDTO(exerciseId, 453.5, 10, 10);
        User user = new User();

        Workout workout = new Workout();
        workout.setId(workoutId);

        WorkoutLog workoutLog = new WorkoutLog();
        workoutLog.setWorkout(workout);

        Mockito.when(workoutLogRepository.findByIdAndUser(workoutLogId, user)).thenReturn(Optional.of(workoutLog));
        Mockito.when(workoutExerciseRepository.findByWorkoutIdAndExerciseId(workoutId, exerciseId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> workoutLogExerciseService.addExercise(workoutLogId, dto, user));
    }

    @Test
    void lancaErroQuandoExercicioNaoEncontrado(){
        UUID workoutLogId = UUID.randomUUID();
        UUID workoutId = UUID.randomUUID();
        UUID exerciseId = UUID.randomUUID();
        WorkoutLogExerciseRequestDTO dto = new WorkoutLogExerciseRequestDTO(exerciseId, 453.5, 10, 10);
        User user = new User();

        Workout workout = new Workout();
        workout.setId(workoutId);

        WorkoutLog workoutLog = new WorkoutLog();
        workoutLog.setWorkout(workout);

        Mockito.when(workoutLogRepository.findByIdAndUser(workoutLogId, user)).thenReturn(Optional.of(workoutLog));
        Mockito.when(workoutExerciseRepository.findByWorkoutIdAndExerciseId(workoutId, exerciseId)).thenReturn(Optional.of(new WorkoutExercise()));
        Mockito.when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> workoutLogExerciseService.addExercise(workoutLogId, dto, user));
    }

    @Test
    void lancaErroQuandoExercicioJaFoiRegistrado(){
        UUID workoutLogId = UUID.randomUUID();
        UUID workoutId = UUID.randomUUID();
        UUID exerciseId = UUID.randomUUID();
        WorkoutLogExerciseRequestDTO dto = new WorkoutLogExerciseRequestDTO(exerciseId, 453.5, 10, 10);
        User user = new User();

        Workout workout = new Workout();
        workout.setId(workoutId);

        WorkoutLog workoutLog = new WorkoutLog();
        workoutLog.setWorkout(workout);

        Mockito.when(workoutLogRepository.findByIdAndUser(workoutLogId, user)).thenReturn(Optional.of(workoutLog));
        Mockito.when(workoutExerciseRepository.findByWorkoutIdAndExerciseId(workoutId, exerciseId)).thenReturn(Optional.of(new WorkoutExercise()));
        Mockito.when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.of(new Exercise()));
        Mockito.when(workoutLogExerciseRepository.findByWorkoutLogIdAndExerciseId(workoutLogId, exerciseId)).thenReturn(Optional.of(new WorkoutLogExercise()));

        assertThrows(BusinessException.class, () -> workoutLogExerciseService.addExercise(workoutLogId, dto, user));
    }
}
