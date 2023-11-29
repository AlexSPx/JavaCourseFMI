package bg.sofia.uni.fmi.mjt.simcity.plot;

import bg.sofia.uni.fmi.mjt.simcity.exception.BuildableAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.simcity.exception.BuildableNotFoundException;
import bg.sofia.uni.fmi.mjt.simcity.exception.InsufficientPlotAreaException;
import bg.sofia.uni.fmi.mjt.simcity.property.buildable.Buildable;

import java.util.HashMap;
import java.util.Map;

public class Plot<E extends Buildable> implements PlotAPI<Buildable> {
    private final HashMap<String, Buildable> buildings;
    private final int capacity;
    private int constructed;

    public Plot(int buildableArea) {
        this.buildings = new HashMap<>();
        this.capacity = buildableArea;
        this.constructed = 0;
    }

    @Override
    public void construct(String address, Buildable buildable) {
        if (address == null || address.isBlank() || buildable == null) {
            throw new IllegalArgumentException("Address or buildable could not be null or empty");
        }

        if (constructed + buildable.getArea() > capacity) {
            throw new InsufficientPlotAreaException();
        }

        if (buildings.putIfAbsent(address, buildable) != null) {
            throw new BuildableAlreadyExistsException();
        }

        constructed += buildable.getArea();
    }

    @Override
    public void constructAll(Map<String, Buildable> buildables) {
        if (buildables == null || buildables.isEmpty()) {
            throw new IllegalArgumentException("Buildables could not be null or empty");
        }

        int neededSpace = 0;

        for (String key : buildables.keySet()) {
            neededSpace += buildables.get(key).getArea();

            if (constructed + neededSpace > capacity) {
                throw new InsufficientPlotAreaException();
            }

            if (buildings.containsKey(key)) {
                throw new BuildableAlreadyExistsException();
            }
        }

        buildings.putAll(buildables);
        constructed += neededSpace;
    }

    @Override
    public void demolish(String address) {
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("address could not be null or blank");
        }

        Buildable build = buildings.remove(address);

        if (build == null) {
            throw new BuildableNotFoundException();
        }

        constructed -= build.getArea();
    }

    @Override
    public void demolishAll() {
        buildings.clear();
        this.constructed = 0;
    }

    @Override
    public Map<String, Buildable> getAllBuildables() {
        return Map.copyOf(buildings);
    }

    @Override
    public int getRemainingBuildableArea() {
        return this.capacity - this.constructed;
    }
}
