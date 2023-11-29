package bg.sofia.uni.fmi.mjt.intelligenthome.storage;

import bg.sofia.uni.fmi.mjt.intelligenthome.device.WiFiThermostat;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;


public class MapDeviceStorageTest {
    @Test
    void storeTest() {
        MapDeviceStorage storage = new MapDeviceStorage();
        storage.store("13", null);

        assertEquals(1, storage.listAll().size());
    }

    @Test
    void deleteTest() {
        MapDeviceStorage storage = new MapDeviceStorage();

        storage.store("13", new WiFiThermostat("ww", 12, LocalDateTime.now()));
        storage.delete("13");

        assertEquals(false, storage.exists("13"));
    }
}
