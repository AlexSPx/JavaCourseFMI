package bg.sofia.uni.fmi.mjt.gym.member.comparators;

import bg.sofia.uni.fmi.mjt.gym.member.Address;
import bg.sofia.uni.fmi.mjt.gym.member.GymMember;

import java.util.Comparator;

public class MemberGymDistanceComparator implements Comparator<GymMember> {
    private final Address gymAddress;

    public MemberGymDistanceComparator(Address gymAddress) {
        this.gymAddress = gymAddress;
    }

    @Override
    public int compare(GymMember o1, GymMember o2) {
        return Double.compare(
                gymAddress.getDistanceTo(o1.getAddress()),
                gymAddress.getDistanceTo(o2.getAddress()));
    }
}
