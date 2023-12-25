package bg.sofia.uni.fmi.mjt.space.mission;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MissionStatusTest {
    @Test
    void testValueOfLabelInvalidLabel() {
        assertNull(MissionStatus.valueOfLabel("Nothing"));
    }
}