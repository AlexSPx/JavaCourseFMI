package bg.sofia.uni.fmi.mjt.space.rocket.comparator;

import bg.sofia.uni.fmi.mjt.space.rocket.Rocket;

import java.util.Comparator;

public class RocketHeightComparator implements Comparator<Rocket> {
    @Override
    public int compare(Rocket o1, Rocket o2) {
        if (o1.height().isPresent() && o2.height().isPresent()) {
            return o2.height().get().compareTo(o1.height().get());
        } else if (o1.height().isPresent()) {
            return -1;
        } else if (o2.height().isPresent()) {
            return 1;
        } else {
            return 0;
        }
    }
}
