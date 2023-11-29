package bg.sofia.uni.fmi.mjt.itinerary.comparator;

import bg.sofia.uni.fmi.mjt.itinerary.algorithm.DistancePair;

import java.util.Comparator;

public class CheapestDistanceCost implements Comparator<DistancePair> {

    @Override
    public int compare(DistancePair o1, DistancePair o2) {
        int compare = o1.fCost().compareTo(o2.fCost());
        if (compare == 0) return o1.city().name().compareTo(o2.city().name());
        else return compare;
    }
}
