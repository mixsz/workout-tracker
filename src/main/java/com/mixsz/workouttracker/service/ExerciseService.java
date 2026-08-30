package com.mixsz.workouttracker.service;

import com.mixsz.workouttracker.dto.request.ExerciseRequestDTO;
import com.mixsz.workouttracker.dto.external.NinjaExerciseDTO;
import com.mixsz.workouttracker.enums.MuscleGroup;
import com.mixsz.workouttracker.exception.custom.BusinessException;
import com.mixsz.workouttracker.exception.custom.ResourceNotFoundException;
import com.mixsz.workouttracker.model.Exercise;
import com.mixsz.workouttracker.repository.ExerciseRepository;
import com.mixsz.workouttracker.repository.WorkoutExerciseRepository;
import com.mixsz.workouttracker.repository.WorkoutLogExerciseRepository;
import com.mixsz.workouttracker.specification.ExerciseSpecification;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.UUID;

@Service
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final WorkoutExerciseRepository workoutExerciseRepository;
    private final WorkoutLogExerciseRepository workoutLogExerciseRepository;

    private final RestTemplate restTemplate;

    @Value("${ninja.api.key}")
    private String ninjaApiKey;

    public ExerciseService(ExerciseRepository exerciseRepository,
                           WorkoutExerciseRepository workoutExerciseRepository,
                           WorkoutLogExerciseRepository workoutLogExerciseRepository,
                           RestTemplate restTemplate) {
        this.exerciseRepository = exerciseRepository;
        this.workoutExerciseRepository = workoutExerciseRepository;
        this.workoutLogExerciseRepository = workoutLogExerciseRepository;
        this.restTemplate = restTemplate;
    }

    public List<Exercise> findAll() {
        return exerciseRepository.findAll();
    }


    public Exercise findById(UUID id){
        return exerciseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Exercício não encontrado!"));
    }


    public List<Exercise> search(String name, MuscleGroup muscleGroup){
        Sort sort = Sort.by("name").ascending();
        if(name != null && muscleGroup != null){
            return exerciseRepository.findByNameAndMuscleGroup(name, muscleGroup, sort);
        }
        else if(name != null){
            return exerciseRepository.findByNameContainingIgnoreCase(name, sort);
        }
        else if(muscleGroup != null){
            return exerciseRepository.findByMuscleGroup(muscleGroup, sort);
        }
        else{
            return exerciseRepository.findAll(sort);
        }
    }

    public Page<Exercise> search(String name, List<MuscleGroup> muscleGroups, Pageable pageable) {
        return exerciseRepository.findAll(ExerciseSpecification.search(name, muscleGroups), pageable);
    }


    @Transactional
    public Exercise save(ExerciseRequestDTO exerciseRequestDTO){

        if (exerciseRepository.existsByNameIgnoreCase(exerciseRequestDTO.name().trim())) {
            throw new BusinessException("Exercício já cadastrado!");
        }
        Exercise exercise = new Exercise();
        exercise.setName(exerciseRequestDTO.name().trim());
        exercise.setMuscleGroup(exerciseRequestDTO.muscleGroup());
        exercise.setDescription(exerciseRequestDTO.description().trim());
        return exerciseRepository.save(exercise);
    }


    @Transactional
    public Exercise update(UUID id, ExerciseRequestDTO exerciseRequestDTO){
        Exercise exercise = this.findById(id);

        String newName = exerciseRequestDTO.name().trim();
        if (exerciseRepository.existsByNameIgnoreCaseAndIdNot(newName, id)) {
            throw new BusinessException("Exercício já cadastrado!");
        }

        exercise.setName(newName);
        exercise.setMuscleGroup(exerciseRequestDTO.muscleGroup());
        exercise.setDescription(exerciseRequestDTO.description().trim());
        return exerciseRepository.save(exercise);
    }


    @Transactional
    public void delete(UUID id){
        Exercise exercise = this.findById(id);
        if (workoutExerciseRepository.existsByExercise(exercise)
                || workoutLogExerciseRepository.existsByExercise(exercise)) {
            throw new BusinessException("Este exercício está vinculado a um treino ou histórico de treino.");
        }
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
            String name = dto.name().trim();
            if (!exerciseRepository.existsByNameIgnoreCase(name)) {
                Exercise exercise = new Exercise();
                exercise.setName(name);
                exercise.setMuscleGroup(MuscleGroup.valueOf(dto.muscle().toUpperCase().replace(" ", "_")));
                exercise.setDescription(dto.instructions() != null ? dto.instructions() : "");
                exerciseRepository.save(exercise);
            }
        }
    }

}
