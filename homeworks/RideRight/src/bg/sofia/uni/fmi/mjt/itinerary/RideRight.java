package bg.sofia.uni.fmi.mjt.itinerary;

import bg.sofia.uni.fmi.mjt.itinerary.algorithm.CheapestRouteAStar;
import bg.sofia.uni.fmi.mjt.itinerary.exception.CityNotKnownException;
import bg.sofia.uni.fmi.mjt.itinerary.exception.NoPathToDestinationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SequencedCollection;

public class RideRight implements ItineraryPlanner {
    private final Map<City, List<Journey>> map;

    public RideRight(List<Journey> schedule) {
        this.map = new HashMap<>();

        for (Journey edge : schedule) {

            if (map.containsKey(edge.from())) {
                map.get(edge.from()).add(edge);
            } else {
                List<Journey> nodeEdges = new ArrayList<>();
                nodeEdges.add(edge);

                map.put(edge.from(), nodeEdges);
            }

            if (!map.containsKey(edge.to())) {
                map.put(edge.to(), new ArrayList<>());
            }
        }
    }

    @Override
    public SequencedCollection<Journey> findCheapestPath(City start, City destination, boolean allowTransfer)
            throws CityNotKnownException, NoPathToDestinationException {
        if (start == null || destination == null) {
            throw new IllegalArgumentException();
        }

        if (!map.containsKey(start) || !map.containsKey(destination)) {
            throw new CityNotKnownException("City does not exist");
        }

        CheapestRouteAStar algorithm = new CheapestRouteAStar(map);

        final int flatKmCost = 20;
        if (allowTransfer) {
            return algorithm.findCheapestPath(start, destination, flatKmCost);
        } else {
            return algorithm.checkForDirect(start, destination);
        }
    }
}
