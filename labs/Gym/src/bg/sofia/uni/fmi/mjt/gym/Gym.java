package bg.sofia.uni.fmi.mjt.gym;

import bg.sofia.uni.fmi.mjt.gym.member.Address;
import bg.sofia.uni.fmi.mjt.gym.member.GymMember;
import bg.sofia.uni.fmi.mjt.gym.member.comparators.MemberGymDistanceComparator;
import bg.sofia.uni.fmi.mjt.gym.member.comparators.MemberNameComparator;
import bg.sofia.uni.fmi.mjt.gym.workout.Exercise;
import bg.sofia.uni.fmi.mjt.gym.workout.Workout;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class Gym implements GymAPI {
    private final Address address;
    private final SortedSet<GymMember> members;
    private int remainingMembers;
    public Gym(int capacity, Address address) {
        this.address = address;
        remainingMembers = capacity;
        this.members = new TreeSet<>();
    }

    @Override
    public SortedSet<GymMember> getMembers() {
        return this.members;
    }

    @Override
    public SortedSet<GymMember> getMembersSortedByName() {
        SortedSet<GymMember> sortedMembers = new TreeSet<>(new MemberNameComparator());
        sortedMembers.addAll(members);
        return sortedMembers;
    }

    @Override
    public SortedSet<GymMember> getMembersSortedByProximityToGym() {
        SortedSet<GymMember> sortedMembers = new TreeSet<>(new MemberGymDistanceComparator(address));
        sortedMembers.addAll(members);
        return sortedMembers;
    }

    @Override
    public void addMember(GymMember member) throws GymCapacityExceededException {
        if (member == null) throw new IllegalArgumentException();

        if (remainingMembers == 0) {
            throw new GymCapacityExceededException();
        }

        remainingMembers--;
        this.members.add(member);
    }

    @Override
    public void addMembers(Collection<GymMember> members) throws GymCapacityExceededException {
        if (members == null || members.isEmpty()) throw new IllegalArgumentException();

        int requestSize = members.size();

        if (remainingMembers - requestSize < 0) {
            throw new GymCapacityExceededException();
        }

        remainingMembers -= requestSize;
        this.members.addAll(members);
    }

    @Override
    public boolean isMember(GymMember member) {
        if (member == null) throw new IllegalArgumentException();

        return this.members.contains(member);
    }

    @Override
    public boolean isExerciseTrainedOnDay(String exerciseName, DayOfWeek day) {
        if (exerciseName == null || exerciseName.isEmpty() || day == null) throw new IllegalArgumentException();

        for (GymMember member : members) {
            Workout workout = member.getTrainingProgram().get(day);

            if (workout != null && workout.exercises().contains(new Exercise(exerciseName, 0, 0))) return true;
        }

        return false;
    }

    @Override
    public Map<DayOfWeek, List<String>> getDailyListOfMembersForExercise(String exerciseName) {
        if (exerciseName == null || exerciseName.isEmpty()) throw new IllegalArgumentException();

        Map<DayOfWeek, List<String>> membersTrained = new EnumMap<>(DayOfWeek.class);

        Exercise search = new Exercise(exerciseName, 0, 0);

        for (DayOfWeek day : DayOfWeek.values()) {
            List<String> currMembers = new ArrayList<>();
            for (GymMember member : members) {
                Workout workout = member.getTrainingProgram().get(day);

                if (workout != null && workout.exercises().contains(search)) {
                    currMembers.add(member.getName());
                }
            }

            if (!currMembers.isEmpty()) {
                membersTrained.put(day, currMembers);
            }
        }

        return membersTrained;
    }
}
