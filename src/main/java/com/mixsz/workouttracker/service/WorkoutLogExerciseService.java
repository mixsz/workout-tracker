package com.mixsz.workouttracker.service;

import com.mixsz.workouttracker.dto.request.WorkoutLogExerciseRequestDTO;
import com.mixsz.workouttracker.exception.custom.BusinessException;
import com.mixsz.workouttracker.exception.custom.ResourceNotFoundException;
import com.mixsz.workouttracker.model.*;
import com.mixsz.workouttracker.repository.ExerciseRepository;
import com.mixsz.workouttracker.repository.WorkoutExerciseRepository;
import com.mixsz.workouttracker.repository.WorkoutLogExerciseRepository;
import com.mixsz.workouttracker.repository.WorkoutLogRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class WorkoutLogExerciseService {

    private final WorkoutLogExerciseRepository workoutLogExerciseRepository;
    private final WorkoutLogRepository workoutLogRepository;
    private final ExerciseRepository exerciseRepository;
    private final WorkoutExerciseRepository workoutExerciseRepository;

    public WorkoutLogExerciseService(WorkoutLogExerciseRepository workoutLogExerciseRepository,
                                     WorkoutLogRepository workoutLogRepository,
                                     ExerciseRepository exerciseRepository,
                                     WorkoutExerciseRepository workoutExerciseRepository) {
        this.workoutLogExerciseRepository = workoutLogExerciseRepository;
        this.workoutLogRepository = workoutLogRepository;
        this.exerciseRepository = exerciseRepository;
        this.workoutExerciseRepository = workoutExerciseRepository;
    }

    public List<WorkoutLogExercise> findAll(UUID workoutLogId, User user) {
        WorkoutLog workoutLog = workoutLogRepository.findByIdAndUser(workoutLogId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Registro de treino não encontrado!"));

        return workoutLogExerciseRepository.findByWorkoutLogOrderByPositionAsc(workoutLog);
    }

    public WorkoutLogExercise findById(UUID workoutLogId, UUID exerciseId, User user) {
        if (workoutLogRepository.findByIdAndUser(workoutLogId, user).isEmpty()) {
            throw new ResourceNotFoundException("Registro de treino não encontrado!");
        }

        return workoutLogExerciseRepository.findByWorkoutLogIdAndExerciseId(workoutLogId, exerciseId)
                .orElseThrow(() -> new ResourceNotFoundException("Exercício não encontrado no registro de treino!"));
    }

    @Transactional
    public WorkoutLogExercise addExercise(UUID workoutLogId, WorkoutLogExerciseRequestDTO dto, User user) {

        WorkoutLog workoutLog = workoutLogRepository.findByIdAndUser(workoutLogId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Registro de treino não encontrado!"));

        WorkoutExercise workoutExercise = workoutExerciseRepository.findByWorkoutIdAndExerciseId(workoutLog.getWorkout().getId(),
                                                                                                dto.exerciseId())
                .orElseThrow(() -> new ResourceNotFoundException("Exercício não pertence a esse treino!"));

        Exercise exercise = exerciseRepository.findById(dto.exerciseId())
                .orElseThrow(() -> new ResourceNotFoundException("Exercício não encontrado!"));

        if(workoutLogExerciseRepository.findByWorkoutLogIdAndExerciseId(workoutLogId, dto.exerciseId()).isPresent()){
            throw new BusinessException("Exercício já registrado!");
        }

        WorkoutLogExercise workoutLogExercise = new WorkoutLogExercise();
        workoutLogExercise.setExercise(exercise);
        workoutLogExercise.setWorkoutLog(workoutLog);
        workoutLogExercise.setRepsDone(dto.repsDone());
        workoutLogExercise.setSetsDone(dto.setsDone());
        workoutLogExercise.setWeightDone(dto.weightDone());
        workoutLogExercise.setPosition(workoutExercise.getPosition());
        return workoutLogExerciseRepository.save(workoutLogExercise);
    }

    @Transactional
    public WorkoutLogExercise update(WorkoutLogExerciseRequestDTO dto, UUID workoutLogId,  UUID exerciseId, User user){
        WorkoutLogExercise workoutLogExercise = findById(workoutLogId, exerciseId, user);

        workoutLogExercise.setRepsDone(dto.repsDone());
        workoutLogExercise.setSetsDone(dto.setsDone());
        workoutLogExercise.setWeightDone(dto.weightDone());

        return workoutLogExerciseRepository.save(workoutLogExercise);
    }

    @Transactional
    public void delete(UUID workoutLogId, UUID exerciseId, User user){
        WorkoutLogExercise workoutLogExercise = findById(workoutLogId, exerciseId, user);
        workoutLogExerciseRepository.delete(workoutLogExercise);
    }

}
