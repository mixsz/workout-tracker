package com.mixsz.workouttracker.service;

import com.mixsz.workouttracker.dto.request.ReorderWorkoutExerciseRequestDTO;
import com.mixsz.workouttracker.dto.request.WorkoutExerciseRequestDTO;
import com.mixsz.workouttracker.exception.custom.BusinessException;
import com.mixsz.workouttracker.exception.custom.ResourceNotFoundException;
import com.mixsz.workouttracker.model.User;
import com.mixsz.workouttracker.model.Workout;
import com.mixsz.workouttracker.model.WorkoutExercise;
import com.mixsz.workouttracker.repository.ExerciseRepository;
import com.mixsz.workouttracker.repository.WorkoutExerciseRepository;
import com.mixsz.workouttracker.repository.WorkoutRepository;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.jdbc.Work;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class WorkoutExerciseServiceTest {

    @Mock
    WorkoutRepository workoutRepository;

    @Mock
    ExerciseRepository exerciseRepository;

    @Mock
    WorkoutExerciseRepository workoutExerciseRepository;

    @InjectMocks
    WorkoutExerciseService workoutExerciseService;

    @Test
    void lancaErroQuandoNaoEncontraTreino(){
        UUID workoutId = UUID.randomUUID();
        User user = new User();

        Mockito.when(workoutRepository.findByIdAndUser(workoutId, user)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> workoutExerciseService.findAll(workoutId, user));
    }

    @Test
    void lancaErroQuandoNaoEncontraExercicioDeUmTreino(){
        UUID workoutId = UUID.randomUUID();
        UUID exerciseId = UUID.randomUUID();
        Workout workout = new Workout();
        User user = new User();

        Mockito.when(workoutRepository.findByIdAndUser(workoutId, user)).thenReturn(Optional.of(workout));
        Mockito.when(workoutExerciseRepository.findByWorkoutIdAndExerciseId(workoutId, exerciseId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> workoutExerciseService.findById(workoutId, exerciseId, user));
    }

    @Test
    void retornaUmExercicioDeUmTreinoQuandoEncontrado(){
        UUID workoutId = UUID.randomUUID();
        UUID exerciseId = UUID.randomUUID();
        Workout workout = new Workout();
        User user = new User();
        WorkoutExercise workoutExercise = new WorkoutExercise();

        Mockito.when(workoutRepository.findByIdAndUser(workoutId, user)).thenReturn(Optional.of(workout));
        Mockito.when(workoutExerciseRepository.findByWorkoutIdAndExerciseId(workoutId, exerciseId)).thenReturn(Optional.of(workoutExercise));

       WorkoutExercise res = workoutExerciseService.findById(workoutId, exerciseId, user);

       assertEquals(workoutExercise, res);
    }

    @Test
    void lancaErroQuandoExercicioJaAdicionadoNoTreino(){
        UUID workoutId = UUID.randomUUID();
        UUID exerciseId = UUID.randomUUID();
        Workout workout = new Workout();
        User user = new User();
        WorkoutExerciseRequestDTO dto = new WorkoutExerciseRequestDTO(exerciseId, 3, 10);


        Mockito.when(workoutRepository.findByIdAndUser(workoutId, user)).thenReturn(Optional.of(workout));
        Mockito.when(workoutExerciseRepository.findByWorkoutIdAndExerciseId(workoutId, exerciseId))
                .thenReturn(Optional.of(new WorkoutExercise()));

        assertThrows(RuntimeException.class, () -> workoutExerciseService.addExercise(workoutId, dto, user));
    }

    @Test
    void lancaErroQuandoExercicioNaoEncontradoNoCatalogo(){
        UUID workoutId = UUID.randomUUID();
        WorkoutExerciseRequestDTO dto = new WorkoutExerciseRequestDTO(UUID.randomUUID(), 3, 10);
        User user = new User();

        Mockito.when(workoutRepository.findByIdAndUser(workoutId, user)).thenReturn(Optional.of(new Workout()));
        Mockito.when(workoutExerciseRepository.findByWorkoutIdAndExerciseId(workoutId, dto.exerciseId()))
                .thenReturn(Optional.empty());
        Mockito.when(exerciseRepository.findById(dto.exerciseId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> workoutExerciseService.addExercise(workoutId, dto, user));
    }

    @Test
    void lancaErroQuandoQuantidadeDeExerciciosNaoBatem(){
        UUID workoutId = UUID.randomUUID();
        ReorderWorkoutExerciseRequestDTO dto = new ReorderWorkoutExerciseRequestDTO(
                List.of(UUID.randomUUID(), UUID.randomUUID()));
        User user = new User();
        Workout workout = new Workout();

        Mockito.when(workoutRepository.findByIdAndUser(workoutId, user)).thenReturn(Optional.of(workout));
        Mockito.when(workoutExerciseRepository.countByWorkout(workout)).thenReturn(1);

        assertThrows(BusinessException.class, () -> workoutExerciseService.reorderExercises(workoutId, dto, user));
    }

    @Test
    void lancaErroQuandoNaoEncontraExercicioDeUmTreinoVersaoArrayList(){
        UUID workoutId = UUID.randomUUID();
        ReorderWorkoutExerciseRequestDTO dto = new ReorderWorkoutExerciseRequestDTO(
                List.of(UUID.randomUUID(), UUID.randomUUID()));
        User user = new User();
        Workout workout = new Workout();

        Mockito.when(workoutRepository.findByIdAndUser(workoutId, user)).thenReturn(Optional.of(workout));
        Mockito.when(workoutExerciseRepository.countByWorkout(workout)).thenReturn(2);
        Mockito.when(workoutExerciseRepository.findByWorkoutIdAndExerciseId(workoutId, dto.exerciseIds().get(0)))
                .thenReturn((Optional.of(new WorkoutExercise())));
        Mockito.when(workoutExerciseRepository.findByWorkoutIdAndExerciseId(workoutId, dto.exerciseIds().get(1)))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> workoutExerciseService.reorderExercises(workoutId, dto, user));
    }

    @Test
    void retornaTodosOsExerciciosDeUmTreinoAposReordenacao(){
        UUID workoutId = UUID.randomUUID();
        ReorderWorkoutExerciseRequestDTO dto = new ReorderWorkoutExerciseRequestDTO(
                List.of(UUID.randomUUID(), UUID.randomUUID()));
        User user = new User();
        Workout workout = new Workout();
        WorkoutExercise we1 = new WorkoutExercise();
        WorkoutExercise we2 = new WorkoutExercise();

        Mockito.when(workoutRepository.findByIdAndUser(workoutId, user)).thenReturn(Optional.of(workout));
        Mockito.when(workoutExerciseRepository.countByWorkout(workout)).thenReturn(2);
        Mockito.when(workoutExerciseRepository.findByWorkoutIdAndExerciseId(workoutId, dto.exerciseIds().get(0)))
                .thenReturn(Optional.of(we1));
        Mockito.when(workoutExerciseRepository.findByWorkoutIdAndExerciseId(workoutId, dto.exerciseIds().get(1)))
                .thenReturn(Optional.of(we2));
        Mockito.when(workoutExerciseRepository.saveAll(Mockito.anyList()))
                .thenReturn(List.of(we1, we2));

        List<WorkoutExercise> res = workoutExerciseService.reorderExercises(workoutId, dto, user);
        assertEquals(2, res.size());
        assertEquals(0, we1.getPosition());
        assertEquals(1, we2.getPosition());
    }

    @Test
    void retornaExerciciosReordenadosAposRemocaoDeUmExercicio(){
        UUID workoutId = UUID.randomUUID();
        UUID exerciseId = UUID.randomUUID();
        User user = new User();
        Workout workout = new Workout();
        WorkoutExercise we1 = new WorkoutExercise();
        we1.setPosition(0);
        WorkoutExercise we2 = new WorkoutExercise();
        we2.setPosition(1);
        we1.setWorkout(workout);
        we2.setWorkout(workout);

        Mockito.when(workoutRepository.findByIdAndUser(workoutId, user)).thenReturn(Optional.of(workout));
        Mockito.when(workoutExerciseRepository.findByWorkoutIdAndExerciseId(workoutId, exerciseId))
                .thenReturn(Optional.of(we1));
        Mockito.when(workoutExerciseRepository.findByWorkoutAndPositionGreaterThan(workout, 0))
                .thenReturn(List.of(we2));

        workoutExerciseService.removeExercise(workoutId, exerciseId, user);

        assertEquals(0, we2.getPosition());
    }
}
