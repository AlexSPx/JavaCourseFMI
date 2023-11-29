package bg.sofia.uni.fmi.mjt.itinerary.algorithm;

import bg.sofia.uni.fmi.mjt.itinerary.City;
import bg.sofia.uni.fmi.mjt.itinerary.Journey;
import bg.sofia.uni.fmi.mjt.itinerary.comparator.CheapestDistanceCost;
import bg.sofia.uni.fmi.mjt.itinerary.comparator.JourneyCostAfterTax;
import bg.sofia.uni.fmi.mjt.itinerary.exception.NoPathToDestinationException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

public class CheapestRouteAStar {
    private final Map<City, List<Journey>> map;

    public CheapestRouteAStar(Map<City, List<Journey>> map) {
        this.map = map;
    }

    private List<Journey> getPath(DistancePair current) {
        List<Journey> path = new ArrayList<>();
        while (current.journey() != null) {
            path.add(current.journey());
            current = current.previous();
        }
        Collections.reverse(path);
        return path;
    }

    private BigDecimal calcFlatDistanceCost(City a, City b, double flatCostKm) {
        final int metersInKilometer = 1000;
        return new BigDecimal((Math.abs(a.location().x() - b.location().x()) +
                Math.abs(a.location().y() - b.location().y())) * flatCostKm / metersInKilometer);
    }

    private BigDecimal calcEdgeCost(Journey edge) {
        return edge.price().add(
                edge.price().multiply(edge.vehicleType().getGreenTax())
        );
    }

    public List<Journey> checkForDirect(City from, City to) throws NoPathToDestinationException {
        Journey cheapest = null;

        JourneyCostAfterTax comparator = new JourneyCostAfterTax();

        for (Journey journey : map.get(from)) {
            if (journey.to().equals(to)) {
                if (cheapest == null) cheapest = journey;
                cheapest = comparator.compare(cheapest, journey) < 0 ? cheapest : journey;
            }
        }

        if (cheapest == null) {
            throw new NoPathToDestinationException("No path without transfers from " + from + " to " + to);
        }

        return List.of(cheapest);
    }

    public List<Journey> findCheapestPath(City from, City to, double flatCostKm)
            throws NoPathToDestinationException {
        Queue<DistancePair> openSet = new PriorityQueue<>(new CheapestDistanceCost());
        Map<City, BigDecimal> gScore = new HashMap<>();

        openSet.add(new DistancePair(from, null, null, calcFlatDistanceCost(from, to, flatCostKm)));
        gScore.put(from, new BigDecimal(0));

        while (!openSet.isEmpty()) {
            DistancePair current = openSet.poll();

            if (current.city().equals(to)) {
                return getPath(current);
            }

            for (Journey edge : map.get(current.city())) {
                BigDecimal gCost = gScore.get(current.city()).add(calcEdgeCost(edge));
                BigDecimal hCost = calcFlatDistanceCost(edge.to(), to, flatCostKm);

                DistancePair neighbour = new DistancePair(edge.to(), edge, current, hCost.add(gCost));

                if (!gScore.containsKey(neighbour.city()) || gCost.compareTo(gScore.get(neighbour.city())) < 0 ) {
                    gScore.put(neighbour.city(), gCost);
                    openSet.add(neighbour);
                }
            }
        }

        throw new NoPathToDestinationException("No path was found from " + from + " to " + to);
    }
}
