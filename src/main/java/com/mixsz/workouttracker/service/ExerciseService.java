package com.mixsz.workouttracker.service;

import com.mixsz.workouttracker.dto.request.ExerciseRequestDTO;
import com.mixsz.workouttracker.dto.external.NinjaExerciseDTO;
import com.mixsz.workouttracker.enums.MuscleGroup;
import com.mixsz.workouttracker.exception.custom.BusinessException;
import com.mixsz.workouttracker.exception.custom.ResourceNotFoundException;
import com.mixsz.workouttracker.model.Exercise;
import com.mixsz.workouttracker.repository.ExerciseRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@Service
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;

    private final RestTemplate restTemplate;

    @Value("${ninja.api.key}")
    private String ninjaApiKey;

    public ExerciseService(ExerciseRepository exerciseRepository, RestTemplate restTemplate) {
        this.exerciseRepository = exerciseRepository;
        this.restTemplate = restTemplate;
    }

    public List<Exercise> findAll() {
        return exerciseRepository.findAll();
    }


    public Exercise findById(UUID id){
        return exerciseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Exercício não encontrado!"));
    }


    public List<Exercise> search(String name, MuscleGroup muscleGroup){
        if(name != null && muscleGroup != null){
            return exerciseRepository.findByNameAndMuscleGroup(name, muscleGroup);
        }
        else if(name != null){
            return exerciseRepository.findByNameContainingIgnoreCase(name);
        }
        else if(muscleGroup != null){
            return exerciseRepository.findByMuscleGroup(muscleGroup);
        }
        else{
            return this.findAll();
        }
    }


    @Transactional
    public Exercise save(ExerciseRequestDTO exerciseRequestDTO){

        if(exerciseRepository.findByName(exerciseRequestDTO.name().trim()).isPresent()) {
            throw new BusinessException("Exercício já cadastrado!");
        }
        Exercise exercise = new Exercise();
        exercise.setName(exerciseRequestDTO.name().trim());
        exercise.setMuscleGroup(exerciseRequestDTO.muscleGroup());
        return exerciseRepository.save(exercise);
    }


    @Transactional
    public Exercise update(UUID id, ExerciseRequestDTO exerciseRequestDTO){
        Exercise exercise = this.findById(id);
        exercise.setName(exerciseRequestDTO.name().trim());
        exercise.setMuscleGroup(exerciseRequestDTO.muscleGroup());
        return exerciseRepository.save(exercise);
    }


    @Transactional
    public void delete(UUID id){
        Exercise exercise = this.findById(id);
        exerciseRepository.delete(exercise);
    }


    @Transactional
    public void importFromApi(String muscle) {
        String url = "https://api.api-ninjas.com/v1/exercises?muscle=" + muscle;

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Api-Key", ninjaApiKey);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<NinjaExerciseDTO[]> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, NinjaExerciseDTO[].class
        );

        for (NinjaExerciseDTO dto : response.getBody()) {
            if (exerciseRepository.findByName(dto.name()).isEmpty()) {
                Exercise exercise = new Exercise();
                exercise.setName(dto.name());
                exercise.setMuscleGroup(MuscleGroup.valueOf(dto.muscle().toUpperCase().replace(" ", "_")));
                exerciseRepository.save(exercise);
            }
        }
    }

}
