package com.mixsz.workouttracker.specification;

import com.mixsz.workouttracker.enums.MuscleGroup;
import com.mixsz.workouttracker.model.Exercise;
import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExerciseSpecification {

    public static Specification<Exercise> search(String name, List<MuscleGroup> muscleGroups) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (name != null && !name.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }

            if (muscleGroups != null && !muscleGroups.isEmpty()) {
                predicates.add(root.get("muscleGroup").in(muscleGroups));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}