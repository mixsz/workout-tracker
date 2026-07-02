package com.mixsz.workouttracker.enums;

public enum MuscleGroup {
    ABDOMINALS("Abdominais"),
    ABDUCTORS("Abdutores"),
    ADDUCTORS("Adutores"),
    BICEPS("Bíceps"),
    CALVES("Panturrilha"),
    CHEST("Peito"),
    FOREARMS("Antebraço"),
    GLUTES("Glúteos"),
    HAMSTRINGS("Posterior de coxa"),
    LATS("Latíssimo"),
    LOWER_BACK("Lombar"),
    MIDDLE_BACK("Dorsal médio"),
    NECK("Pescoço"),
    QUADRICEPS("Quadríceps"),
    TRAPS("Trapézio"),
    TRICEPS("Tríceps"),
    SHOULDERS("Ombros");

    private String displayName;

    MuscleGroup(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
