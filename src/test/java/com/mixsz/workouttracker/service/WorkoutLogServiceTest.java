package com.mixsz.workouttracker.service;

import com.mixsz.workouttracker.exception.custom.ResourceNotFoundException;
import com.mixsz.workouttracker.model.User;
import com.mixsz.workouttracker.model.Workout;
import com.mixsz.workouttracker.model.WorkoutLogExercise;
import com.mixsz.workouttracker.repository.WorkoutLogRepository;
import com.mixsz.workouttracker.repository.WorkoutRepository;
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
public class WorkoutLogServiceTest {

    @Mock
    WorkoutLogRepository workoutLogRepository;

    @Mock
    WorkoutRepository workoutRepository;

    @InjectMocks
    WorkoutLogService workoutLogService;

    @Test
    void lancaErroQuandoNaoEncontraTreino(){
        UUID workoutId = UUID.randomUUID();
        User user = new User();

        Mockito.when(workoutRepository.findByIdAndUser(workoutId, user)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> workoutLogService.findByWorkout(workoutId, user));
    }

    @Test
    void lancaErroQuandoNaoEncontraTreinoParaData(){
        UUID workoutId = UUID.randomUUID();
        User user = new User();

        Mockito.when(workoutRepository.findByIdAndUser(workoutId, user)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> workoutLogService.findByDateBetweenAndWorkout(null, null, workoutId, user));
    }

    @Test
    void lancaErroQuandoNaoEncontraRegistroDeTreino(){
        UUID workoutLogId = UUID.randomUUID();
        User user = new User();

        Mockito.when(workoutLogRepository.findByIdAndUser(workoutLogId, user)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> workoutLogService.deleteWorkoutLog(workoutLogId, user));
    }

    @Test
    void finalFelizQuandoAdicionaRegistroDeTreino(){
        UUID workoutId = UUID.randomUUID();
        User user = new User();
        Workout workout = new Workout();
        workout.setId(workoutId);
        workout.setUser(user);

        Mockito.when(workoutRepository.findByIdAndUser(workoutId, user)).thenReturn(Optional.of(workout));

        workoutLogService.addWorkoutLog(workoutId, user);

        Mockito.verify(workoutLogRepository, Mockito.times(1)).save(Mockito.any());
    }
}
