package com.mixsz.workouttracker.controller;

import com.mixsz.workouttracker.dto.response.WorkoutLogResponseDTO;
import com.mixsz.workouttracker.model.User;
import com.mixsz.workouttracker.service.WorkoutLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/workoutLog")
public class WorkoutLogController {

    private final WorkoutLogService workoutLogService;

    public WorkoutLogController(WorkoutLogService workoutLogService) {
        this.workoutLogService = workoutLogService;
    }

    @GetMapping
    public ResponseEntity<List<WorkoutLogResponseDTO>> getAllWorkoutLogs() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<WorkoutLogResponseDTO> workoutLogs = workoutLogService.findAll(user)
                .stream()
                .map(log -> new WorkoutLogResponseDTO(
                        log.getId(),
                        log.getWorkout().getId(),
                        log.getWorkout().getTitle(),
                        log.getDate()
                ))
                .toList();

        return ResponseEntity.ok(workoutLogs);
    }

    @GetMapping("/{workoutId}")
    public ResponseEntity<List<WorkoutLogResponseDTO>> getWorkoutLogsByWorkout(@PathVariable UUID workoutId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<WorkoutLogResponseDTO> workoutLogs = workoutLogService.findByWorkout(workoutId, user)
                .stream()
                .map(log -> new WorkoutLogResponseDTO(
                        log.getId(),
                        log.getWorkout().getId(),
                        log.getWorkout().getTitle(),
                        log.getDate()
                ))
                .toList();

        return ResponseEntity.ok(workoutLogs);
    }

    @GetMapping("/date")
    public ResponseEntity<List<WorkoutLogResponseDTO>> getWorkoutLogByDate(@RequestParam LocalDate date){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<WorkoutLogResponseDTO> workoutLogs = workoutLogService.findByDate(date, user)
                .stream()
                .map(log -> new WorkoutLogResponseDTO(
                        log.getId(),
                        log.getWorkout().getId(),
                        log.getWorkout().getTitle(),
                        log.getDate()
                ))
                .toList();

        return ResponseEntity.ok(workoutLogs);
    }

    @GetMapping("/date/between")
    public ResponseEntity <List<WorkoutLogResponseDTO>> getWorkoutLogBetweenDate(
                                                        @RequestParam LocalDate start,
                                                        @RequestParam LocalDate end){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<WorkoutLogResponseDTO> workoutLogs = workoutLogService.findByDateBetween(start, end, user)
                .stream()
                .map(log -> new WorkoutLogResponseDTO(
                        log.getId(),
                        log.getWorkout().getId(),
                        log.getWorkout().getTitle(),
                        log.getDate()
                ))
                .toList();

        return ResponseEntity.ok(workoutLogs);
    }

    @GetMapping("/date/between/{workoutId}")
    public ResponseEntity <List<WorkoutLogResponseDTO>> getWorkoutLogBetweenDateByWorkout(
                                                        @PathVariable UUID workoutId,
                                                        @RequestParam LocalDate start,
                                                        @RequestParam LocalDate end){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<WorkoutLogResponseDTO> workoutLogs = workoutLogService.findByDateBetweenAndWorkout(start, end, workoutId, user)
                .stream()
                .map(log -> new WorkoutLogResponseDTO(
                        log.getId(),
                        log.getWorkout().getId(),
                        log.getWorkout().getTitle(),
                        log.getDate()
                ))
                .toList();

        return ResponseEntity.ok(workoutLogs);
    }

    @PostMapping("/{workoutId}")
    public ResponseEntity<WorkoutLogResponseDTO> addWorkoutLog(@PathVariable UUID workoutId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var workoutLog = workoutLogService.addWorkoutLog(workoutId, user);
        WorkoutLogResponseDTO workoutLogResponse = new WorkoutLogResponseDTO(
                workoutLog.getId(),
                workoutLog.getWorkout().getId(),
                workoutLog.getWorkout().getTitle(),
                workoutLog.getDate()
        );

        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(workoutLogResponse);
    }

    @DeleteMapping("/{workoutLogId}")
    public ResponseEntity<Void> deleteWorkoutLog(@PathVariable UUID workoutLogId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        workoutLogService.deleteWorkoutLog(workoutLogId, user);
        return ResponseEntity.noContent().build();
    }
}
