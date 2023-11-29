package bg.sofia.uni.fmi.mjt.gym;

import bg.sofia.uni.fmi.mjt.gym.member.Address;
import bg.sofia.uni.fmi.mjt.gym.member.Gender;
import bg.sofia.uni.fmi.mjt.gym.member.GymMember;
import bg.sofia.uni.fmi.mjt.gym.member.Member;
import bg.sofia.uni.fmi.mjt.gym.workout.Exercise;
import bg.sofia.uni.fmi.mjt.gym.workout.Workout;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

public class Main {
    @SuppressWarnings("checkstyle:MagicNumber")
    public static void main(String[] args) {
        GymAPI gym = new Gym(100, new Address(12, 13));

        try {
            GymMember member1 = new Member(
                    new Address(1,1 ),
                    "Alex",
                    20,
                    "ID12",
                    Gender.MALE
            );

            List<Exercise> exes = new ArrayList<>();
            exes.add(new Exercise("test1", 12, 12));

            member1.setWorkout(DayOfWeek.MONDAY, new Workout(exes));

            member1.addExercise(DayOfWeek.MONDAY, new Exercise("test", 12, 12));

            gym.addMember(member1);

            System.out.println(member1.getDaysFinishingWith("test1"));
        } catch (GymCapacityExceededException e) {
            throw new RuntimeException(e);
        }
    }
}
