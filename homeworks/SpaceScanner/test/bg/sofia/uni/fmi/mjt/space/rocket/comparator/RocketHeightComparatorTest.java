package bg.sofia.uni.fmi.mjt.space.rocket.comparator;

import bg.sofia.uni.fmi.mjt.space.rocket.Rocket;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RocketHeightComparatorTest {
    @Test
    void testOneEmptyOptional() {
        Rocket rocket1 = Rocket.of("0,Tsyklon-3,https://en.wikipedia.org/wiki/Tsyklon-3,39.0 m");
        Rocket rocket2 = Rocket.of("109,Ceres-1,,");
        assertEquals(-1, new RocketHeightComparator().compare(rocket1, rocket2));
    }
}