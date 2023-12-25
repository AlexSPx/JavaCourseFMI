package bg.sofia.uni.fmi.mjt.space.rocket;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RocketTest {
    @Test
    void testRocketOfAllOptionalEmpty() {
        Rocket rocket = Rocket.of("0,Atlas-E/F Star-37S-ISS,,");

        assertEquals(new Rocket("0", "Atlas-E/F Star-37S-ISS", Optional.empty(), Optional.empty()),
                rocket);
    }
}