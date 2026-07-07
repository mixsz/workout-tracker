package com.mixsz.workouttracker.controller;

import com.mixsz.workouttracker.dto.request.ExerciseRequestDTO;
import com.mixsz.workouttracker.dto.response.ExerciseResponseDTO;
import com.mixsz.workouttracker.enums.MuscleGroup;
import com.mixsz.workouttracker.model.Exercise;
import com.mixsz.workouttracker.service.ExerciseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/exercise")
public class ExerciseController {

    private final ExerciseService exerciseService;

    public ExerciseController(ExerciseService exerciseService){
        this.exerciseService = exerciseService;
    }

    @GetMapping
    public ResponseEntity<List<ExerciseResponseDTO>> getAll(@RequestParam(required = false) String name,
                                                            @RequestParam(required = false) MuscleGroup muscleGroup) {

        List<ExerciseResponseDTO> exercises = exerciseService.search(name, muscleGroup)
                .stream()
                .map(e -> new ExerciseResponseDTO(e.getId(), e.getName(), e.getMuscleGroup()))
                .toList();
        return ResponseEntity.ok(exercises);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExerciseResponseDTO> getById(@PathVariable UUID id) {
        Exercise exercise = exerciseService.findById(id);
        return ResponseEntity.ok(new ExerciseResponseDTO(exercise.getId(), exercise.getName(), exercise.getMuscleGroup()));
    }

    @PostMapping
    public ResponseEntity<ExerciseResponseDTO> save(@RequestBody @Valid ExerciseRequestDTO exerciseRequestDTO){
        Exercise exercise = exerciseService.save(exerciseRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ExerciseResponseDTO(exercise.getId(), exercise.getName(), exercise.getMuscleGroup())
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExerciseResponseDTO> update(@PathVariable UUID id,
                                                      @RequestBody @Valid ExerciseRequestDTO exerciseRequestDTO) {
        Exercise exercise = exerciseService.update(id, exerciseRequestDTO);
        return ResponseEntity.ok(new ExerciseResponseDTO(exercise.getId(), exercise.getName(), exercise.getMuscleGroup()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        exerciseService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/import")
    public ResponseEntity<Void> importFromApi(@RequestParam String muscle) {
        exerciseService.importFromApi(muscle);
        return ResponseEntity.ok().build();
    }

}
