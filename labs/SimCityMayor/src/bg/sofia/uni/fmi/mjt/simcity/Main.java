package bg.sofia.uni.fmi.mjt.simcity;

import bg.sofia.uni.fmi.mjt.simcity.plot.Plot;
import bg.sofia.uni.fmi.mjt.simcity.plot.PlotAPI;
import bg.sofia.uni.fmi.mjt.simcity.property.buildable.Buildable;
import bg.sofia.uni.fmi.mjt.simcity.property.buildable.BuildableType;

import java.util.HashMap;
import java.util.Map;

class TestBuilding implements Buildable {

    @Override
    public BuildableType getType() {
        return BuildableType.COMMERCIAL;
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Override
    public int getArea() {
        return 50;
    }
}

public class Main {
    public static void main(String[] args) {
        Map<String, Buildable> builds = new HashMap<>();

        builds.put("1", new TestBuilding());
        builds.put("2", new TestBuilding());
        builds.put("3", new TestBuilding());
        builds.put("4", new TestBuilding());
        builds.put("5", new TestBuilding());
        builds.put("6", new TestBuilding());
        builds.put("7", new TestBuilding());

        PlotAPI<Buildable> plot = new Plot<>(0);
        plot.constructAll(builds);
    }
}
