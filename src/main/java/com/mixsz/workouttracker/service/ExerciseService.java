package com.mixsz.workouttracker.service;

import com.mixsz.workouttracker.dto.request.ExerciseRequestDTO;
import com.mixsz.workouttracker.dto.response.NinjaExerciseDTO;
import com.mixsz.workouttracker.enums.MuscleGroup;
import com.mixsz.workouttracker.model.ExerciseModel;
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


    @Transactional
    public ExerciseModel save(ExerciseRequestDTO exerciseRequestDTO){

        if(exerciseRepository.findByName(exerciseRequestDTO.name()).isPresent()) {
            throw new RuntimeException("Exercício já cadastrado!");
        }
        ExerciseModel exercise = new ExerciseModel();
        exercise.setName(exerciseRequestDTO.name());
        exercise.setMuscleGroup(exerciseRequestDTO.muscleGroup());
        return exerciseRepository.save(exercise);
    }

    public List<ExerciseModel> findAll() {
        return exerciseRepository.findAll();
    }

    public ExerciseModel findById(UUID id){
        return exerciseRepository.findById(id).orElseThrow(() -> new RuntimeException("Exercício não encontrado!"));
    }

    public List<ExerciseModel> search(String name, MuscleGroup muscleGroup){
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
    public ExerciseModel update(UUID id, ExerciseRequestDTO exerciseRequestDTO){
        ExerciseModel exercise = this.findById(id);
        exercise.setName(exerciseRequestDTO.name());
        exercise.setMuscleGroup(exerciseRequestDTO.muscleGroup());
        return exerciseRepository.save(exercise);
    }

    @Transactional
    public void delete(UUID id){
        ExerciseModel exercise = this.findById(id);
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
                ExerciseModel exercise = new ExerciseModel();
                exercise.setName(dto.name());
                exercise.setMuscleGroup(MuscleGroup.valueOf(dto.muscle().toUpperCase().replace(" ", "_")));
                exerciseRepository.save(exercise);
            }
        }
    }

}
