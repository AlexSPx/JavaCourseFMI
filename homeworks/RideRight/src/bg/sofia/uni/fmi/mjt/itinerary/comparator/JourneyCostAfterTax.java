package bg.sofia.uni.fmi.mjt.itinerary.comparator;

import bg.sofia.uni.fmi.mjt.itinerary.Journey;

import java.math.BigDecimal;
import java.util.Comparator;

public class JourneyCostAfterTax implements Comparator<Journey> {
    private BigDecimal afterTax(Journey o) {
        return o.price().add(
                o.price().multiply(o.vehicleType().getGreenTax())
        );
    }

    @Override
    public int compare(Journey o1, Journey o2) {
        return afterTax(o1).compareTo(afterTax(o2));
    }
}
