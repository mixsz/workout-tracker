package com.mixsz.workouttracker.service;

import com.mixsz.workouttracker.dto.request.WorkoutExerciseRequestDTO;
import com.mixsz.workouttracker.model.Exercise;
import com.mixsz.workouttracker.model.User;
import com.mixsz.workouttracker.model.Workout;
import com.mixsz.workouttracker.model.WorkoutExercise;
import com.mixsz.workouttracker.repository.ExerciseRepository;
    import com.mixsz.workouttracker.repository.WorkoutExerciseRepository;
import com.mixsz.workouttracker.repository.WorkoutRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

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
                .orElseThrow(() -> new RuntimeException("Treino não encontrado!"));
        return workoutExerciseRepository.findByWorkout(workout);
    }

    public WorkoutExercise findById(UUID workoutId, UUID exerciseId, User user){
        if(workoutRepository.findByIdAndUser(workoutId, user).isEmpty()) {
            throw new RuntimeException("Treino não encontrado!");
        }

        return workoutExerciseRepository.findByWorkoutIdAndExerciseId(workoutId, exerciseId)
                .orElseThrow(() -> new RuntimeException("Exercício não encontrado no treino!"));
    }

    @Transactional
    public WorkoutExercise addExercise(UUID workoutId, WorkoutExerciseRequestDTO dto, User user){
        Workout workout = workoutRepository.findByIdAndUser(workoutId, user)
                .orElseThrow(() -> new RuntimeException("Treino não encontrado!"));

        if(workoutExerciseRepository.findByWorkoutIdAndExerciseId(workoutId, dto.exerciseId()).isPresent()){
            throw new RuntimeException("Exercício já adicionado ao treino!");
        }

        Exercise exercise = exerciseRepository.findById(dto.exerciseId())
                .orElseThrow(() -> new RuntimeException("Exercício não encontrado!"));

        WorkoutExercise workoutExercise = new WorkoutExercise();
        workoutExercise.setWorkout(workout);
        workoutExercise.setExercise(exercise);
        workoutExercise.setSets(dto.sets());
        workoutExercise.setReps(dto.reps());
        return workoutExerciseRepository.save(workoutExercise);
    }

    @Transactional
    public WorkoutExercise updateExercise(UUID workoutId, UUID exerciseId, WorkoutExerciseRequestDTO dto, User user){

        if(workoutRepository.findByIdAndUser(workoutId, user).isEmpty()) {
            throw new RuntimeException("Treino não encontrado!");
        }

        WorkoutExercise workoutExercise = workoutExerciseRepository.findByWorkoutIdAndExerciseId(workoutId, exerciseId)
                .orElseThrow(() -> new RuntimeException("Exercício não encontrado no treino!"));

        workoutExercise.setSets(dto.sets());
        workoutExercise.setReps(dto.reps());
        return workoutExerciseRepository.save(workoutExercise);

    }

    @Transactional
    public void removeExercise(UUID workoutId, UUID exerciseId, User user){

        if(workoutRepository.findByIdAndUser(workoutId, user).isEmpty()) {
            throw new RuntimeException("Treino não encontrado!");
        }

        WorkoutExercise workoutExercise = workoutExerciseRepository.findByWorkoutIdAndExerciseId(workoutId, exerciseId)
                .orElseThrow(() -> new RuntimeException("Exercício não encontrado no treino!"));

        workoutExerciseRepository.delete(workoutExercise);
    }

}
