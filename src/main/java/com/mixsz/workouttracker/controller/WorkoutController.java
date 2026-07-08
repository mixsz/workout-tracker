package com.mixsz.workouttracker.controller;

import com.mixsz.workouttracker.dto.request.ReorderWorkoutRequestDTO;
import com.mixsz.workouttracker.dto.request.WorkoutRequestDTO;
import com.mixsz.workouttracker.dto.response.WorkoutResponseDTO;
import com.mixsz.workouttracker.model.User;
import com.mixsz.workouttracker.model.Workout;
import com.mixsz.workouttracker.service.WorkoutService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/workout")
public class WorkoutController {

    private final WorkoutService workoutService;

    public WorkoutController(WorkoutService workoutService){
        this.workoutService = workoutService;
    }

    @GetMapping
    public ResponseEntity<List<WorkoutResponseDTO>> getAll() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<WorkoutResponseDTO> workouts = workoutService.findAll(user)
                .stream()
                .map(w -> new WorkoutResponseDTO(w.getId(), w.getTitle(), w.getPosition()))
                .toList();
        return ResponseEntity.ok(workouts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkoutResponseDTO> getById(@PathVariable UUID id){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Workout workout = workoutService.findById(id, user);
        return ResponseEntity.ok(new WorkoutResponseDTO(workout.getId(), workout.getTitle(), workout.getPosition()));
    }

    @PostMapping
    public ResponseEntity<WorkoutResponseDTO> save(@RequestBody @Valid WorkoutRequestDTO workoutRequestDTO){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Workout workout = workoutService.save(workoutRequestDTO, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new WorkoutResponseDTO(workout.getId(), workout.getTitle(), workout.getPosition()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkoutResponseDTO> update(@PathVariable UUID id, @RequestBody @Valid WorkoutRequestDTO dto ){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Workout workout = workoutService.update(id, dto, user);
        return ResponseEntity.ok(
                new WorkoutResponseDTO(workout.getId(), workout.getTitle(), workout.getPosition()));
    }

    @PutMapping("/reorder")
    public ResponseEntity<List<WorkoutResponseDTO>> reorderWorkouts(@RequestBody @Valid ReorderWorkoutRequestDTO dto){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Workout> workouts = workoutService.reorderWorkouts(dto, user);
        List<WorkoutResponseDTO> response = workouts.stream()
                .map(w -> new WorkoutResponseDTO(w.getId(), w.getTitle(), w.getPosition()))
                .toList();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        workoutService.delete(user, id);
        return ResponseEntity.noContent().build();
    }

}