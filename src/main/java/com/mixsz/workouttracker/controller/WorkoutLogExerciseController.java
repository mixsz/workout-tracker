package com.mixsz.workouttracker.controller;

import com.mixsz.workouttracker.dto.request.WorkoutLogExerciseRequestDTO;
import com.mixsz.workouttracker.dto.response.ExerciseResponseDTO;
import com.mixsz.workouttracker.dto.response.WorkoutLogExerciseResponseDTO;
import com.mixsz.workouttracker.model.User;
import com.mixsz.workouttracker.model.WorkoutLogExercise;
import com.mixsz.workouttracker.service.WorkoutLogExerciseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/workoutLogExercise")
public class WorkoutLogExerciseController {

    private final WorkoutLogExerciseService workoutLogExerciseService;

    public WorkoutLogExerciseController(WorkoutLogExerciseService workoutLogExerciseService) {
        this.workoutLogExerciseService = workoutLogExerciseService;
    }

    @GetMapping("/{workoutLogId}")
    public ResponseEntity<List<WorkoutLogExerciseResponseDTO>> getAllWorkoutLogExercises(@PathVariable UUID workoutLogId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<WorkoutLogExerciseResponseDTO> exercises = workoutLogExerciseService.findAll(workoutLogId, user)
                .stream()
                .map(e -> new WorkoutLogExerciseResponseDTO(
                        e.getId(),
                        new ExerciseResponseDTO(e.getExercise().getId(), e.getExercise().getName(), e.getExercise().getMuscleGroup()),
                        e.getWeightDone(),
                        e.getSetsDone(),
                        e.getRepsDone()
                ))
                .toList();
        return ResponseEntity.ok(exercises);
    }

    @GetMapping("{workoutLogId}/{exerciseId}")
    public ResponseEntity<WorkoutLogExerciseResponseDTO> getWorkoutLogExercise(@PathVariable UUID workoutLogId,
                                                                               @PathVariable UUID exerciseId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        WorkoutLogExercise workoutLogExercise = workoutLogExerciseService.findById(workoutLogId, exerciseId, user);
        WorkoutLogExerciseResponseDTO response = new WorkoutLogExerciseResponseDTO(
                workoutLogExercise.getId(),
                new ExerciseResponseDTO(workoutLogExercise.getExercise().getId(),
                        workoutLogExercise.getExercise().getName(), workoutLogExercise.getExercise().getMuscleGroup()),
                workoutLogExercise.getWeightDone(),
                workoutLogExercise.getSetsDone(),
                workoutLogExercise.getRepsDone()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{workoutLogId}")
    public ResponseEntity<WorkoutLogExerciseResponseDTO> addWorkoutLogExercise(@PathVariable UUID workoutLogId,
                                                               @RequestBody WorkoutLogExerciseRequestDTO dto) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        WorkoutLogExercise workoutLogExercise = workoutLogExerciseService.addExercise(workoutLogId, dto, user);
        WorkoutLogExerciseResponseDTO response = new WorkoutLogExerciseResponseDTO(
                workoutLogExercise.getId(),
                new ExerciseResponseDTO(workoutLogExercise.getExercise().getId(),
                        workoutLogExercise.getExercise().getName(), workoutLogExercise.getExercise().getMuscleGroup()),
                workoutLogExercise.getWeightDone(),
                workoutLogExercise.getSetsDone(),
                workoutLogExercise.getRepsDone()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{workoutLogId}/{exerciseId}")
    public ResponseEntity<WorkoutLogExerciseResponseDTO> updateWorkoutLogExercise(@PathVariable UUID workoutLogId,
                                                                             @PathVariable UUID exerciseId,
                                                                             @RequestBody WorkoutLogExerciseRequestDTO dto) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        WorkoutLogExercise workoutLogExercise = workoutLogExerciseService.update(dto, workoutLogId, exerciseId, user);
        WorkoutLogExerciseResponseDTO response = new WorkoutLogExerciseResponseDTO(
                workoutLogExercise.getId(),
                new ExerciseResponseDTO(workoutLogExercise.getExercise().getId(),
                        workoutLogExercise.getExercise().getName(), workoutLogExercise.getExercise().getMuscleGroup()),
                workoutLogExercise.getWeightDone(),
                workoutLogExercise.getSetsDone(),
                workoutLogExercise.getRepsDone()
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{workoutLogId}/{exerciseId}")
    public ResponseEntity<Void> deleteWorkoutLogExercise(@PathVariable UUID workoutLogId,
                                                        @PathVariable UUID exerciseId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        workoutLogExerciseService.delete(workoutLogId, exerciseId, user);
        return ResponseEntity.noContent().build();
    }

}