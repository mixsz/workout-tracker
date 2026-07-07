package com.mixsz.workouttracker.controller;

import com.mixsz.workouttracker.dto.request.WorkoutExerciseRequestDTO;
import com.mixsz.workouttracker.dto.response.ExerciseResponseDTO;
import com.mixsz.workouttracker.dto.response.WorkoutExerciseResponseDTO;
import com.mixsz.workouttracker.model.User;
import com.mixsz.workouttracker.service.WorkoutExerciseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/workoutExercise")
public class WorkoutExerciseController {

    private final WorkoutExerciseService workoutExerciseService;

    public WorkoutExerciseController(WorkoutExerciseService workoutExerciseService) {
        this.workoutExerciseService = workoutExerciseService;
    }

    @GetMapping("/{workoutId}")
    public ResponseEntity<List<WorkoutExerciseResponseDTO>> getAll(@PathVariable UUID workoutId){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<WorkoutExerciseResponseDTO> workoutExercises = workoutExerciseService.findAll(workoutId, user)
                .stream()
                .map(we -> new WorkoutExerciseResponseDTO(
                        we.getId(),
                        we.getSets(),
                        we.getReps(),
                        new ExerciseResponseDTO(
                                we.getExercise().getId(),
                                we.getExercise().getName(),
                                we.getExercise().getMuscleGroup()
                        )
                ))
                .toList();


        return ResponseEntity.ok(workoutExercises);
    }

    @GetMapping("/{workoutId}/{exerciseId}")
    public ResponseEntity<WorkoutExerciseResponseDTO> getById(@PathVariable UUID workoutId, @PathVariable UUID exerciseId){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        var workoutExercise = workoutExerciseService.findById(workoutId, exerciseId, user);

        WorkoutExerciseResponseDTO responseDTO = new WorkoutExerciseResponseDTO(
                workoutExercise.getId(),
                workoutExercise.getSets(),
                workoutExercise.getReps(),
                new ExerciseResponseDTO(
                        workoutExercise.getExercise().getId(),
                        workoutExercise.getExercise().getName(),
                        workoutExercise.getExercise().getMuscleGroup()
                )
        );

        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/{workoutId}")
    public ResponseEntity<WorkoutExerciseResponseDTO> addExercise(@PathVariable UUID workoutId,
                                                                  @RequestBody @Valid WorkoutExerciseRequestDTO dto){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        var workoutExercise = workoutExerciseService.addExercise(workoutId, dto, user);

        WorkoutExerciseResponseDTO responseDTO = new WorkoutExerciseResponseDTO(
                workoutExercise.getId(),
                workoutExercise.getSets(),
                workoutExercise.getReps(),
                new ExerciseResponseDTO(
                        workoutExercise.getExercise().getId(),
                        workoutExercise.getExercise().getName(),
                        workoutExercise.getExercise().getMuscleGroup()
                )
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @PatchMapping("/{workoutId}/{exerciseId}")
    public ResponseEntity<WorkoutExerciseResponseDTO> update(@PathVariable UUID workoutId,
                                                             @PathVariable UUID exerciseId,
                                                             @RequestBody @Valid WorkoutExerciseRequestDTO dto){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        var workoutExercise = workoutExerciseService.updateExercise(workoutId, exerciseId, dto, user);

        WorkoutExerciseResponseDTO responseDTO = new WorkoutExerciseResponseDTO(
                workoutExercise.getId(),
                workoutExercise.getSets(),
                workoutExercise.getReps(),
                new ExerciseResponseDTO(
                        workoutExercise.getExercise().getId(),
                        workoutExercise.getExercise().getName(),
                        workoutExercise.getExercise().getMuscleGroup()
                )
        );

        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{workoutId}/{exerciseId}")
    public ResponseEntity<Void> delete(@PathVariable UUID workoutId, @PathVariable UUID exerciseId){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        workoutExerciseService.removeExercise(workoutId, exerciseId, user);

        return ResponseEntity.noContent().build();
    }
}