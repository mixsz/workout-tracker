package com.mixsz.workouttracker.enums;

public enum MuscleGroup {
    CHEST("Peito"),
    BACK("Costa"),
    SHOULDERS("Ombro"),
    TRICEPS("Tríceps"),
    BICEPS("Bíceps"),
    LEGS("Perna"),
    TRAPEZIUS("Trapézio");

    private String displayName;

    MuscleGroup(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
