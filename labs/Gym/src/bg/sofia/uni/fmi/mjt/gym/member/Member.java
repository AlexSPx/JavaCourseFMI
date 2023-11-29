package bg.sofia.uni.fmi.mjt.gym.member;

import bg.sofia.uni.fmi.mjt.gym.workout.Exercise;
import bg.sofia.uni.fmi.mjt.gym.workout.Workout;

import java.time.DayOfWeek;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Member implements GymMember, Comparable<Member> {
    private final String personalIdNumber;
    private final String name;
    private final Address address;
    private final int age;
    private final Gender gender;
    private final Map<DayOfWeek, Workout> program;

    public Member(Address address, String name, int age, String personalIdNumber, Gender gender) {
        this.personalIdNumber = personalIdNumber;
        this.name = name;
        this.address = address;
        this.age = age;
        this.gender = gender;
        this.program = new EnumMap<>(DayOfWeek.class);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getAge() {
        return this.age;
    }

    @Override
    public String getPersonalIdNumber() {
        return this.personalIdNumber;
    }

    @Override
    public Gender getGender() {
        return this.gender;
    }

    @Override
    public Address getAddress() {
        return this.address;
    }

    @Override
    public Map<DayOfWeek, Workout> getTrainingProgram() {
        return program;
    }

    @Override
    public void setWorkout(DayOfWeek day, Workout workout) {
        if (workout == null || day == null) throw new IllegalArgumentException();
        program.put(day, workout);
    }

    @Override
    public Collection<DayOfWeek> getDaysFinishingWith(String exerciseName) {
        if (exerciseName == null || exerciseName.isEmpty()) throw new IllegalArgumentException();

        Collection<DayOfWeek> days = new HashSet<>();

        for (DayOfWeek day : program.keySet()) {
            Workout workout = program.get(day);

            if (workout.exercises().getLast().name().equals(exerciseName)) {
                days.add(day);
            }
        }

        return days;
    }

    @Override
    public void addExercise(DayOfWeek day, Exercise exercise) throws DayOffException {
        if (exercise == null || day == null) throw new IllegalArgumentException();
        if (program.get(day) == null) throw new DayOffException();
        program.get(day).exercises().add(exercise);
    }

    @Override
    public void addExercises(DayOfWeek day, List<Exercise> exercises) throws DayOffException {
        if (exercises == null || exercises.isEmpty() || day == null) throw new IllegalArgumentException();
        if (program.get(day) == null) throw new DayOffException();
        program.get(day).exercises().addAll(exercises);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return Objects.equals(personalIdNumber, member.personalIdNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(personalIdNumber);
    }

    @Override
    public int compareTo(Member o) {
        return this.personalIdNumber.compareTo(o.personalIdNumber);
    }
}
