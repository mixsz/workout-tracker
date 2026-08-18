package com.mixsz.workouttracker.service;

import com.mixsz.workouttracker.dto.response.WorkoutLogHistoryResponseDTO;
import com.mixsz.workouttracker.exception.custom.BusinessException;
import com.mixsz.workouttracker.exception.custom.ResourceNotFoundException;
import com.mixsz.workouttracker.model.User;
import com.mixsz.workouttracker.model.Workout;
import com.mixsz.workouttracker.model.WorkoutLog;
import com.mixsz.workouttracker.repository.WorkoutLogExerciseRepository;
import com.mixsz.workouttracker.repository.WorkoutLogRepository;
import com.mixsz.workouttracker.repository.WorkoutRepository;
import com.mixsz.workouttracker.specification.WorkoutLogSpecification;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class WorkoutLogService {

    private final WorkoutLogRepository workoutLogRepository;
    private final WorkoutRepository workoutRepository;
    private final WorkoutLogExerciseRepository workoutLogExerciseRepository;


    public WorkoutLogService(WorkoutLogRepository workoutLogRepository, WorkoutRepository workoutRepository,
                             WorkoutLogExerciseRepository workoutLogExerciseRepository) {
        this.workoutLogRepository = workoutLogRepository;
        this.workoutRepository = workoutRepository;
        this.workoutLogExerciseRepository = workoutLogExerciseRepository;
    }

    public Page<WorkoutLog> search(User user, UUID workoutId,
                                   LocalDateTime start, LocalDateTime end,
                                   boolean includeDeleted, boolean onlyDeleted,
                                   Pageable pageable) {

        if (workoutId != null) {
            workoutRepository.findByIdAndUser(workoutId, user)
                    .orElseThrow(() -> new ResourceNotFoundException("Treino não encontrado!"));
        }

        Specification<WorkoutLog> spec = WorkoutLogSpecification.search(
                user, workoutId, start, end, includeDeleted, onlyDeleted);

        return workoutLogRepository.findAll(spec, pageable);
    }

    public Page<WorkoutLogHistoryResponseDTO> searchWithCounts(
            User user, UUID workoutId,
            LocalDateTime start, LocalDateTime end,
            boolean includeDeleted, boolean onlyDeleted,
            Pageable pageable) {

        Page<WorkoutLog> page = search(user, workoutId, start, end, includeDeleted, onlyDeleted, pageable);

        List<UUID> logIds = page.getContent().stream()
                .map(WorkoutLog::getId)
                .toList();

        Map<UUID, WorkoutLogExerciseRepository.WorkoutLogExerciseCountProjection> countsById =
                workoutLogExerciseRepository.countByWorkoutLogIds(logIds)
                        .stream()
                        .collect(Collectors.toMap(
                                WorkoutLogExerciseRepository.WorkoutLogExerciseCountProjection::getWorkoutLogId,
                                c -> c
                        ));

        return page.map(log -> {
            var counts = countsById.get(log.getId());
            long planned = counts != null ? counts.getPlanned() : 0;
            long done = counts != null ? counts.getDone() : 0;

            return new WorkoutLogHistoryResponseDTO(
                    log.getId(),
                    log.getWorkout() != null ? log.getWorkout().getId() : null,
                    log.getWorkout() != null ? log.getWorkout().getTitle() : log.getWorkoutTitleSnapshot(),
                    log.getDate(),
                    log.getFinishedAt(),
                    planned,
                    done
            );
        });
    }

    public List<WorkoutLog> findAll(User user){
        return workoutLogRepository.findByUserAndFinishedAtIsNotNullOrderByDateDesc(user);
    }

    public List<WorkoutLog> findByWorkout(UUID workoutId, User user){
        Workout workout = workoutRepository.findByIdAndUser(workoutId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Treino não encontrado!"));

        return workoutLogRepository.findByWorkoutAndUserOrderByDateDesc(workout, user);
    }

    public Optional<WorkoutLog> findById(UUID workoutLogId, User user) {
        return workoutLogRepository.findByIdAndUser(workoutLogId, user);
    }

    public List<WorkoutLog> findByDate(LocalDate date, User user) {
        return workoutLogRepository.findByUserAndDateBetweenAndFinishedAtIsNotNullOrderByDateDesc(
                user,
                date.atStartOfDay(),
                date.atTime(23, 59, 59));
    }

    public List<WorkoutLog> findByDateBetween(LocalDate start, LocalDate end, User user) {
        return workoutLogRepository.findByUserAndDateBetweenAndFinishedAtIsNotNullOrderByDateDesc(
                user,
                start.atStartOfDay(),
                end.atTime(23,59,59));
    }

    public List<WorkoutLog> findByDateBetweenAndWorkout(LocalDate start, LocalDate end, UUID workoutId, User user) {
        Workout workout = workoutRepository.findByIdAndUser(workoutId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Treino não encontrado!"));

        return workoutLogRepository.findByWorkoutAndUserAndDateBetweenOrderByDateDesc(
                workout,
                user,
                start.atStartOfDay(),
                end.atTime(23,59,59));
    }

    public WorkoutLog findActive(User user) {
        return workoutLogRepository.findByUserAndFinishedAtIsNull(user).orElse(null);
    }

    @Transactional
    public WorkoutLog finishWorkoutLog(UUID workoutLogId, User user) {
        WorkoutLog log = workoutLogRepository.findByIdAndUser(workoutLogId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Registro não encontrado!"));
        log.setFinishedAt(LocalDateTime.now());
        return workoutLogRepository.save(log);
    }

    @Transactional
    public WorkoutLog addWorkoutLog(UUID workoutId, User user) {
        if (workoutLogRepository.findByUserAndFinishedAtIsNull(user).isPresent()) {
            throw new BusinessException("Você já tem um treino em andamento!");
        }

        Workout workout = workoutRepository.findByIdAndUser(workoutId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Treino não encontrado!"));

        if (workout.getWorkoutExercises().isEmpty()) {
            throw new BusinessException("Treino sem exercícios cadastrados!");
        }

        WorkoutLog workoutLog = new WorkoutLog();
        workoutLog.setWorkout(workout);
        workoutLog.setWorkoutTitleSnapshot(workout.getTitle());
        workoutLog.setUser(user);
        workoutLog.setDate(LocalDateTime.now());

        return workoutLogRepository.save(workoutLog);
    }

    @Transactional
    public void deleteWorkoutLog(UUID workoutLogId, User user){
        WorkoutLog workoutLog = workoutLogRepository.findByIdAndUser(workoutLogId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Registro de treino não encontrado!"));

        workoutLogRepository.delete(workoutLog);
    }
}