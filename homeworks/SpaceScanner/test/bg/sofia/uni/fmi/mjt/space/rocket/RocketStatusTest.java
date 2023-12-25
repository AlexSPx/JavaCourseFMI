package bg.sofia.uni.fmi.mjt.space.rocket;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RocketStatusTest {
    @Test
    void testValueOfLabelInvalidLabel() {
        assertNull(RocketStatus.valueOfLabel("Nothing"));
    }
}