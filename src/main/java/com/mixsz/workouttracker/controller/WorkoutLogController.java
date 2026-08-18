package com.mixsz.workouttracker.controller;

import com.mixsz.workouttracker.dto.response.WorkoutLogHistoryResponseDTO;
import com.mixsz.workouttracker.dto.response.WorkoutLogResponseDTO;
import com.mixsz.workouttracker.exception.custom.ResourceNotFoundException;
import com.mixsz.workouttracker.model.User;
import com.mixsz.workouttracker.model.WorkoutLog;
import com.mixsz.workouttracker.service.WorkoutLogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/workoutLog")
public class WorkoutLogController {

    private final WorkoutLogService workoutLogService;

    public WorkoutLogController(WorkoutLogService workoutLogService) {
        this.workoutLogService = workoutLogService;
    }

    @GetMapping("/history")
    public ResponseEntity<Page<WorkoutLogHistoryResponseDTO>> getHistory(
            @RequestParam(required = false) UUID workoutId,
            @RequestParam(required = false) LocalDate start,
            @RequestParam(required = false) LocalDate end,
            @RequestParam(defaultValue = "false") boolean includeDeleted,
            @RequestParam(defaultValue = "false") boolean onlyDeleted,
            @PageableDefault(size = 5, sort = "date", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        LocalDateTime startDT = start != null ? start.atStartOfDay() : null;
        LocalDateTime endDT = end != null ? end.atTime(23, 59, 59) : null;

        Page<WorkoutLogHistoryResponseDTO> response = workoutLogService.searchWithCounts(
                user, workoutId, startDT, endDT, includeDeleted, onlyDeleted, pageable);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<WorkoutLogResponseDTO>> getAllWorkoutLogs() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<WorkoutLogResponseDTO> workoutLogs = workoutLogService.findAll(user)
                .stream()
                .map(log -> new WorkoutLogResponseDTO(
                        log.getId(),
                        log.getWorkout() != null ? log.getWorkout().getId() : null,
                        log.getWorkout() != null ? log.getWorkout().getTitle() : log.getWorkoutTitleSnapshot(),
                        log.getDate(),
                        log.getFinishedAt()
                ))
                .toList();

        return ResponseEntity.ok(workoutLogs);
    }

    @GetMapping("/session/{workoutLogId}")
    public ResponseEntity<WorkoutLogResponseDTO> getWorkoutLogBySession(@PathVariable UUID workoutLogId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        WorkoutLog workoutLog = workoutLogService.findById(workoutLogId, user).
                orElseThrow(() -> new ResourceNotFoundException("Histórico de treino não encontrado!"));

        WorkoutLogResponseDTO response = new WorkoutLogResponseDTO(
                workoutLog.getId(),
                workoutLog.getWorkout() != null ? workoutLog.getWorkout().getId() : null,
                workoutLog.getWorkout() != null ? workoutLog.getWorkout().getTitle() : workoutLog.getWorkoutTitleSnapshot(),
                workoutLog.getDate(),
                workoutLog.getFinishedAt()
        );
        return ResponseEntity.ok(response);
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
                        log.getDate(),
                        log.getFinishedAt()
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
                        log.getWorkout() != null ? log.getWorkout().getId() : null,
                        log.getWorkout() != null ? log.getWorkout().getTitle() : log.getWorkoutTitleSnapshot(),
                        log.getDate(),
                        log.getFinishedAt()
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
                        log.getWorkout() != null ? log.getWorkout().getId() : null,
                        log.getWorkout() != null ? log.getWorkout().getTitle() : log.getWorkoutTitleSnapshot(),
                        log.getDate(),
                        log.getFinishedAt()
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
                        log.getWorkout() != null ? log.getWorkout().getId() : null,
                        log.getWorkout() != null ? log.getWorkout().getTitle() : log.getWorkoutTitleSnapshot(),
                        log.getDate(),
                        log.getFinishedAt()
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
                workoutLog.getWorkout() != null ? workoutLog.getWorkout().getId() : null,
                workoutLog.getWorkout() != null ? workoutLog.getWorkout().getTitle() : workoutLog.getWorkoutTitleSnapshot(),
                workoutLog.getDate(),
                workoutLog.getFinishedAt()
        );

        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(workoutLogResponse);
    }

    @DeleteMapping("/{workoutLogId}")
    public ResponseEntity<Void> deleteWorkoutLog(@PathVariable UUID workoutLogId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        workoutLogService.deleteWorkoutLog(workoutLogId, user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/active")
    public ResponseEntity<WorkoutLogResponseDTO> getActiveWorkoutLog() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        WorkoutLog log = workoutLogService.findActive(user);
        if (log == null) return ResponseEntity.noContent().build(); // 204, "não tem nenhum"
        return ResponseEntity.ok(new WorkoutLogResponseDTO(
                log.getId(),
                log.getWorkout() != null ? log.getWorkout().getId() : null,
                log.getWorkout() != null ? log.getWorkout().getTitle() : log.getWorkoutTitleSnapshot(),
                log.getDate(),
                log.getFinishedAt()
        ));
    }

    @PatchMapping("/{workoutLogId}/finish")
    public ResponseEntity<WorkoutLogResponseDTO> finishWorkoutLog(@PathVariable UUID workoutLogId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        WorkoutLog log = workoutLogService.finishWorkoutLog(workoutLogId, user);
        return ResponseEntity.ok(new WorkoutLogResponseDTO(
                log.getId(), log.getWorkout() != null ? log.getWorkout().getId() : null,
                log.getWorkout() != null ? log.getWorkout().getTitle() : log.getWorkoutTitleSnapshot(),
                log.getDate(),
                log.getFinishedAt()
        ));
    }

}
