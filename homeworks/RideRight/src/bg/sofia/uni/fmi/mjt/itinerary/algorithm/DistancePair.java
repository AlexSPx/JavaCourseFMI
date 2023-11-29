package bg.sofia.uni.fmi.mjt.itinerary.algorithm;

import bg.sofia.uni.fmi.mjt.itinerary.City;
import bg.sofia.uni.fmi.mjt.itinerary.Journey;

import java.math.BigDecimal;
import java.util.Objects;

public record DistancePair(City city, Journey journey, DistancePair previous, BigDecimal fCost) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DistancePair that = (DistancePair) o;
        return Objects.equals(city, that.city);
    }

    @Override
    public int hashCode() {
        return Objects.hash(city);
    }
}
