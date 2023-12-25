package bg.sofia.uni.fmi.mjt.space.mission.comparator;

import bg.sofia.uni.fmi.mjt.space.mission.Mission;

import java.util.Comparator;

public class HighestCostComparator implements Comparator<Mission> {
    @Override
    public int compare(Mission o1, Mission o2) {
        if (o1.cost().isPresent() && o2.cost().isPresent()) {
            return o2.cost().get().compareTo(o1.cost().get());
        } else if (o1.cost().isPresent()) {
            return -1;
        } else if (o2.cost().isPresent()) {
            return 1;
        } else {
            return 0;
        }
    }
}
