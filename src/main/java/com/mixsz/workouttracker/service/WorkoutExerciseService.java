package com.mixsz.workouttracker.service;

import com.mixsz.workouttracker.dto.request.ReorderWorkoutExerciseRequestDTO;
import com.mixsz.workouttracker.dto.request.WorkoutExerciseRequestDTO;
import com.mixsz.workouttracker.exception.custom.BusinessException;
import com.mixsz.workouttracker.exception.custom.ResourceNotFoundException;
import com.mixsz.workouttracker.model.Exercise;
import com.mixsz.workouttracker.model.User;
import com.mixsz.workouttracker.model.Workout;
import com.mixsz.workouttracker.model.WorkoutExercise;
import com.mixsz.workouttracker.repository.ExerciseRepository;
import com.mixsz.workouttracker.repository.WorkoutExerciseRepository;
import com.mixsz.workouttracker.repository.WorkoutRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class WorkoutExerciseService {

    private final WorkoutRepository workoutRepository;
    private final ExerciseRepository exerciseRepository;
    private final WorkoutExerciseRepository workoutExerciseRepository;

    public WorkoutExerciseService(WorkoutRepository workoutRepository,
                                  ExerciseRepository exerciseRepository,
                                  WorkoutExerciseRepository workoutExerciseRepository) {
        this.workoutRepository = workoutRepository;
        this.exerciseRepository = exerciseRepository;
        this.workoutExerciseRepository = workoutExerciseRepository;
    }

    public List<WorkoutExercise> findAll(UUID workoutId, User user){
        Workout workout = workoutRepository.findByIdAndUser(workoutId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Treino não encontrado!"));
        return workoutExerciseRepository.findByWorkoutOrderByPositionAsc(workout);
    }

    public WorkoutExercise findById(UUID workoutId, UUID exerciseId, User user){
        if(workoutRepository.findByIdAndUser(workoutId, user).isEmpty()) {
            throw new ResourceNotFoundException("Treino não encontrado!");
        }

        return workoutExerciseRepository.findByWorkoutIdAndExerciseId(workoutId, exerciseId)
                .orElseThrow(() -> new ResourceNotFoundException("Exercício não encontrado no treino!"));
    }

    @Transactional
    public WorkoutExercise addExercise(UUID workoutId, WorkoutExerciseRequestDTO dto, User user){
        Workout workout = workoutRepository.findByIdAndUser(workoutId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Treino não encontrado!"));

        if(workoutExerciseRepository.findByWorkoutIdAndExerciseId(workoutId, dto.exerciseId()).isPresent()){
            throw new BusinessException("Exercício já adicionado ao treino!");
        }

        Exercise exercise = exerciseRepository.findById(dto.exerciseId())
                .orElseThrow(() -> new ResourceNotFoundException("Exercício não encontrado no catálogo!"));

        WorkoutExercise workoutExercise = new WorkoutExercise();
        workoutExercise.setWorkout(workout);
        workoutExercise.setExercise(exercise);
        workoutExercise.setSets(dto.sets());
        workoutExercise.setReps(dto.reps());
        workoutExercise.setPosition(workoutExerciseRepository.countByWorkout(workout));
        return workoutExerciseRepository.save(workoutExercise);
    }

    @Transactional
    public WorkoutExercise updateExercise(UUID workoutId, UUID exerciseId, WorkoutExerciseRequestDTO dto, User user){

        if(workoutRepository.findByIdAndUser(workoutId, user).isEmpty()) {
            throw new ResourceNotFoundException("Treino não encontrado!");
        }

        WorkoutExercise workoutExercise = workoutExerciseRepository.findByWorkoutIdAndExerciseId(workoutId, exerciseId)
                .orElseThrow(() -> new ResourceNotFoundException("Exercício não encontrado no treino!"));

        workoutExercise.setSets(dto.sets());
        workoutExercise.setReps(dto.reps());
        return workoutExerciseRepository.save(workoutExercise);

    }

    @Transactional
    public List<WorkoutExercise> reorderExercises(UUID workoutId, ReorderWorkoutExerciseRequestDTO dto, User user) {
        Workout workout = workoutRepository.findByIdAndUser(workoutId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Treino não encontrado!"));

        int totalExercises = workoutExerciseRepository.countByWorkout(workout);
        if (dto.exerciseIds().size() != totalExercises) {
            throw new BusinessException("A quantidade de exercícios não corresponde!");
        }

        List<WorkoutExercise> workoutExercises = new ArrayList<>();
        for (UUID exerciseId : dto.exerciseIds()) {
            WorkoutExercise workoutExercise = workoutExerciseRepository.findByWorkoutIdAndExerciseId(workoutId, exerciseId)
                    .orElseThrow(() -> new ResourceNotFoundException("Exercício não encontrado no treino!"));
            workoutExercises.add(workoutExercise);
        }

        for (int i = 0; i < workoutExercises.size(); i++) {
            workoutExercises.get(i).setPosition(i);
        }

        return workoutExerciseRepository.saveAll(workoutExercises);
    }

    @Transactional
    public void removeExercise(UUID workoutId, UUID exerciseId, User user){

        if(workoutRepository.findByIdAndUser(workoutId, user).isEmpty()) {
            throw new ResourceNotFoundException("Treino não encontrado!");
        }

        WorkoutExercise workoutExercise = workoutExerciseRepository.findByWorkoutIdAndExerciseId(workoutId, exerciseId)
                .orElseThrow(() -> new ResourceNotFoundException("Exercício não encontrado no treino!"));

        int position = workoutExercise.getPosition();
        List<WorkoutExercise> workoutExercises = workoutExerciseRepository.findByWorkoutAndPositionGreaterThan(workoutExercise.getWorkout(), position);
        for(WorkoutExercise we : workoutExercises){
            we.setPosition(we.getPosition() - 1);
            workoutExerciseRepository.save(we);
        }

        workoutExerciseRepository.delete(workoutExercise);
    }

}
