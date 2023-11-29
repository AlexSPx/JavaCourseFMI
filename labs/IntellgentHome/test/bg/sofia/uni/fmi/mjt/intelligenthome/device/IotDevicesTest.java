package bg.sofia.uni.fmi.mjt.intelligenthome.device;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

public class IotDevicesTest {
    @Test
    void IotDeviceSearchEqualsHashCode() {
        IoTDevice aa1 = new AmazonAlexa("aa1", 12, LocalDateTime.now());
        IoTDevice aa2 = new AmazonAlexa("aa2", 12, LocalDateTime.now());

        assertNotEquals(aa1, aa2);
    }
}
