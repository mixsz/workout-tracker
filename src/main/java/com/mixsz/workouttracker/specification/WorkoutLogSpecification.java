package com.mixsz.workouttracker.specification;

import com.mixsz.workouttracker.model.User;
import com.mixsz.workouttracker.model.WorkoutLog;
import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WorkoutLogSpecification {

    public static Specification<WorkoutLog> search(User user, UUID workoutId,
                                                   LocalDateTime start, LocalDateTime end,
                                                   boolean includeDeleted, boolean onlyDeleted) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("user"), user));
            predicates.add(cb.isNotNull(root.get("finishedAt")));

            if (workoutId != null) {
                predicates.add(cb.equal(root.get("workout").get("id"), workoutId));
            }

            if (start != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("date"), start));
            }

            if (end != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("date"), end));
            }

            if (onlyDeleted) {
                predicates.add(cb.isNull(root.get("workout")));
            } else if (workoutId == null && !includeDeleted) {
                predicates.add(cb.isNotNull(root.get("workout")));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}