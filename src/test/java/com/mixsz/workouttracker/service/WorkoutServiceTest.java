package com.mixsz.workouttracker.service;

import com.mixsz.workouttracker.dto.request.ReorderWorkoutRequestDTO;
import com.mixsz.workouttracker.dto.request.WorkoutRequestDTO;
import com.mixsz.workouttracker.exception.custom.BusinessException;
import com.mixsz.workouttracker.exception.custom.ResourceNotFoundException;
import com.mixsz.workouttracker.model.User;
import com.mixsz.workouttracker.model.Workout;
import com.mixsz.workouttracker.repository.WorkoutRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class WorkoutServiceTest {

    @Mock
    WorkoutRepository workoutRepository;

    @InjectMocks
    WorkoutService workoutService;

    @Test
    void lancaErroQuandoNaoEncontraTreino(){
        UUID workoutId = UUID.randomUUID();
        User user = new User();

        Mockito.when(workoutRepository.findByIdAndUser(workoutId, user)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> workoutService.findById(workoutId, user));
    }

    @Test
    void lancaErroQuandoTreinoJaExiste(){
        WorkoutRequestDTO dto = new WorkoutRequestDTO("Treino 1");
        User user = new User();

        Mockito.when(workoutRepository.findByTitleAndUser(dto.title(), user)).thenReturn(Optional.of(new Workout()));

        assertThrows(BusinessException.class, () -> workoutService.save(dto, user));
    }

    @Test
    void lancaErroQuandoTituloJaEstaEmUso(){
        UUID id = UUID.randomUUID();
        WorkoutRequestDTO dto = new WorkoutRequestDTO("Treino 1");
        User user = new User();

        Mockito.when(workoutRepository.findByTitleAndUser(dto.title().trim(), user)).thenReturn(Optional.of(new Workout()));

        assertThrows(BusinessException.class, () -> workoutService.update(id, dto, user));
    }

    @Test
    void lancaErroQuandoAQuantidadeDeTreinosNaoCorresponde(){
        List<UUID> ids = List.of(UUID.randomUUID(), UUID.randomUUID());
        ReorderWorkoutRequestDTO dto = new ReorderWorkoutRequestDTO(ids);
        User user = new User();

        Mockito.when(workoutRepository.findByUserOrderByPositionAsc(user)).thenReturn(List.of(new Workout()));

        assertThrows(BusinessException.class, () -> workoutService.reorderWorkouts(dto, user));
    }

    @Test
    void lancaErroQuandoTreinoNaoEncontradoNaLista(){
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        List<UUID> ids = List.of(id1, id2);
        ReorderWorkoutRequestDTO dto = new ReorderWorkoutRequestDTO(ids);
        User user = new User();

        Workout workout1 = new Workout();
        workout1.setId(id1);

        Workout workout2 = new Workout();
        workout2.setId(UUID.randomUUID());

        Mockito.when(workoutRepository.findByUserOrderByPositionAsc(user)).thenReturn(List.of(workout1, workout2));

        assertThrows(ResourceNotFoundException.class, () -> workoutService.reorderWorkouts(dto, user));
    }
}
