package com.mixsz.workouttracker.service;

import com.mixsz.workouttracker.dto.request.ReorderWorkoutRequestDTO;
import com.mixsz.workouttracker.dto.request.WorkoutRequestDTO;
import com.mixsz.workouttracker.exception.custom.BusinessException;
import com.mixsz.workouttracker.exception.custom.ResourceNotFoundException;
import com.mixsz.workouttracker.model.User;
import com.mixsz.workouttracker.model.Workout;
import com.mixsz.workouttracker.repository.WorkoutRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
public class WorkoutService {

    private final WorkoutRepository workoutRepository;

    public WorkoutService(WorkoutRepository workoutRepository){
        this.workoutRepository = workoutRepository;
    }

    public List<Workout> findAll(User user){
        return workoutRepository.findByUserOrderByPositionAsc(user);
    }


    public Workout findById(UUID id, User user){
        return workoutRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Treino não encontrado!"));
    }


    @Transactional
    public Workout save(WorkoutRequestDTO dto, User user){
        if(workoutRepository.findByTitleAndUser(dto.title(),user).isPresent()){
            throw new BusinessException("Esse treino já existe!");
        }
        Workout workout = new Workout();
        workout.setTitle(dto.title().trim());
        workout.setUser(user);
        workout.setPosition(workoutRepository.countByUser(user));
        return workoutRepository.save(workout);
    }

    @Transactional
    public Workout update(UUID id, WorkoutRequestDTO dto, User user){
        if(workoutRepository.findByTitleAndUser(dto.title().trim(), user).isPresent()){
            throw new BusinessException("Título já está em uso!");
        }
        Workout workout = workoutRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Treino não encontrado!"));
        workout.setTitle(dto.title());
        workout.setPosition(workoutRepository.countByUser(user));
        return workoutRepository.save(workout);
    }

    @Transactional
    public List<Workout> reorderWorkouts(ReorderWorkoutRequestDTO dto, User user){
        List<Workout> workouts = workoutRepository.findByUserOrderByPositionAsc(user);
        if(dto.workoutIds().size() != workouts.size()){
            throw new BusinessException("A quantidade de treinos não corresponde!");
        }
        for(int i = 0; i < dto.workoutIds().size(); i++){
            UUID workoutId = dto.workoutIds().get(i);
            Workout workout = workouts.stream()
                    .filter(w -> w.getId().equals(workoutId))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("Treino não encontrado!"));
            workout.setPosition(i);
            workoutRepository.save(workout);
        }
        return workoutRepository.findByUserOrderByPositionAsc(user);
    }

    @Transactional
    public void delete(User user, UUID id){
        Workout workout = workoutRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Treino não encontrado!"));

        int pos = workout.getPosition();
        List<Workout> workouts = workoutRepository.findByUserAndPositionGreaterThan(user, pos);
        for(Workout w : workouts){
            w.setPosition(w.getPosition() - 1);
            workoutRepository.save(w);
        }

        workoutRepository.delete(workout);
    }

}
