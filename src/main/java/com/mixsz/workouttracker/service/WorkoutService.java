package com.mixsz.workouttracker.service;

import com.mixsz.workouttracker.dto.request.WorkoutRequestDTO;
import com.mixsz.workouttracker.model.User;
import com.mixsz.workouttracker.model.Workout;
import com.mixsz.workouttracker.repository.UserRepository;
import com.mixsz.workouttracker.repository.WorkoutRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
public class WorkoutService {

    private final WorkoutRepository workoutRepository;
    private final UserRepository userRepository;

    public WorkoutService(WorkoutRepository workoutRepository, UserRepository userRepository){
        this.workoutRepository = workoutRepository;
        this.userRepository = userRepository;
    }

    public List<Workout> findAll(User user){
        return workoutRepository.findByUser(user);
    }


    public Workout findById(UUID id){
        return workoutRepository.findById(id).orElseThrow(() -> new RuntimeException("Treino não encontrado!"));
    }


    @Transactional
    public Workout save(WorkoutRequestDTO dto, User user){
        if(workoutRepository.findByTitleAndUser(dto.title(),user).isPresent()){
            throw new RuntimeException("Esse treino já existe!");
        }
        Workout workout = new Workout();
        workout.setTitle(dto.title().trim());
        workout.setUser(user);
        return workoutRepository.save(workout);
    }

    @Transactional
    public Workout update(UUID id, WorkoutRequestDTO dto, User user){
        if(workoutRepository.findByTitleAndUser(dto.title().trim(), user).isPresent()){
            throw new RuntimeException("Título já está em uso!");
        }
        Workout workout = workoutRepository.findById(id).orElseThrow(() -> new RuntimeException("Treino não encontrado!"));
        workout.setTitle(dto.title());
        return workoutRepository.save(workout);
    }

    @Transactional
    public void delete(User user, UUID id){
        Workout workout = workoutRepository.findById(id).orElseThrow(() -> new RuntimeException("Treino não encontrado!"));
        workoutRepository.delete(workout);
    }

}
