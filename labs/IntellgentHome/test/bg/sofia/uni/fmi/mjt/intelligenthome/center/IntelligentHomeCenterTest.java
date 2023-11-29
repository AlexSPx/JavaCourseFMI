package bg.sofia.uni.fmi.mjt.intelligenthome.center;

import bg.sofia.uni.fmi.mjt.intelligenthome.center.exceptions.DeviceAlreadyRegisteredException;
import bg.sofia.uni.fmi.mjt.intelligenthome.center.exceptions.DeviceNotFoundException;
import bg.sofia.uni.fmi.mjt.intelligenthome.device.AmazonAlexa;
import bg.sofia.uni.fmi.mjt.intelligenthome.device.DeviceType;
import bg.sofia.uni.fmi.mjt.intelligenthome.device.IoTDevice;
import bg.sofia.uni.fmi.mjt.intelligenthome.device.RgbBulb;
import bg.sofia.uni.fmi.mjt.intelligenthome.device.WiFiThermostat;
import bg.sofia.uni.fmi.mjt.intelligenthome.storage.DeviceStorage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class IntelligentHomeCenterTest {

    @Mock
    private DeviceStorage storageMock;

    @InjectMocks
    private IntelligentHomeCenter intelligentHomeCenter;

    @Test
    void registerNullDeviceTest() {
        IntelligentHomeCenter intelligentHomeCenter = new IntelligentHomeCenter(storageMock);

        assertThrows(IllegalArgumentException.class,
                () -> intelligentHomeCenter.register(null),
                "Registration of a device, where device is null");
    }

    @Test
    void registerDeviceAlreadyExistsTest() {
        AmazonAlexa device = new AmazonAlexa("alexasname", 12, LocalDateTime.now());
        when(storageMock.exists(device.getId())).thenReturn(true);

        assertThrows(DeviceAlreadyRegisteredException.class,
                () -> intelligentHomeCenter.register(device),
                "Registration of a device, where device is already registered");

        verify(storageMock, never()).store(device.getId(), device);
    }

    @Test
    void registerDeviceSuccessTest() throws DeviceAlreadyRegisteredException {
        AmazonAlexa device = new AmazonAlexa("alexasname", 12, LocalDateTime.now());

        when(storageMock.exists(device.getId())).thenReturn(false);

        intelligentHomeCenter.register(device);

        verify(storageMock, times(1)).store(device.getId(), device);
    }

    @Test
    void unregisterNullDeviceTest() {
        assertThrows(IllegalArgumentException.class,
                () -> intelligentHomeCenter.unregister(null),
                "Unregistered a null device didn't throw IllegalArgumentException");
    }

    @Test
    void unregisterNonExistingDeviceTest() {
        IoTDevice device = new AmazonAlexa("alexasname", 12, LocalDateTime.now());

        when(storageMock.exists(device.getId())).thenReturn(false);

        assertThrows(DeviceNotFoundException.class,
                () -> intelligentHomeCenter.unregister(device),
                "Trying to unregister a non existent device didn't throw DeviceNotFoundException");

        verify(storageMock, never()).delete(device.getId());
    }

    @Test
    void unregisterDeviceSuccessTest() throws DeviceNotFoundException {
        AmazonAlexa device = new AmazonAlexa("alexasname", 12, LocalDateTime.now());

        when(storageMock.exists(device.getId())).thenReturn(true);

        intelligentHomeCenter.unregister(device);

        verify(storageMock, times(1)).delete(device.getId());
    }

    @Test
    void getDeviceByIdNullTest() {
        assertThrows(IllegalArgumentException.class,
                () -> intelligentHomeCenter.getDeviceById(null),
                "Null id didn't throw IllegalArgumentException");
    }

    @Test
    void getDeviceByIdEmptyTest() {
        assertThrows(IllegalArgumentException.class,
                () -> intelligentHomeCenter.getDeviceById(""),
                "Empty id didn't throw IllegalArgumentException");
    }

    @Test
    void getDeviceByIdNotExistTest() {
        when(storageMock.exists("1")).thenReturn(false);

        assertThrows(DeviceNotFoundException.class,
                () -> intelligentHomeCenter.getDeviceById("1"),
                "Test with id that doesn't exist didnt throw DeviceNotFoundException");

        verify(storageMock, never()).delete("1");
    }

    @Test
    void getDeviceByIdSuccessTest() throws DeviceNotFoundException {
        when(storageMock.exists("1")).thenReturn(true);

        intelligentHomeCenter.getDeviceById("1");

        verify(storageMock, times(1)).get("1");
    }

    @Test
    void getDeviceQuantityPerTypeNullTest() {
        assertThrows(IllegalArgumentException.class,
                () -> intelligentHomeCenter.getDeviceQuantityPerType(null),
                "Null type didn't throw IllegalArgumentException");
    }

    @Test
    void getDeviceQuantityPerTypeSuccessTwoTest() {
        IoTDevice device1 = new AmazonAlexa("name1", 12, LocalDateTime.now());
        IoTDevice device2 = new AmazonAlexa("name2", 12, LocalDateTime.now());
        IoTDevice device3 = new RgbBulb("name3", 12, LocalDateTime.now());

        when(storageMock.listAll()).thenReturn(List.of(device1, device2, device3));

        int result = intelligentHomeCenter.getDeviceQuantityPerType(DeviceType.SMART_SPEAKER);

        assertEquals(2, result,
                String.format("expected amount was 2, but was returned %s", result));
    }

    @Test
    void getDeviceQuantityPerTypeSuccessZeroTest() {
        IoTDevice device1 = new AmazonAlexa("name1", 12, LocalDateTime.now());
        IoTDevice device2 = new AmazonAlexa("name2", 12, LocalDateTime.now());
        IoTDevice device3 = new RgbBulb("name3", 12, LocalDateTime.now());

        when(storageMock.listAll()).thenReturn(List.of(device1, device2, device3));

        int result = intelligentHomeCenter.getDeviceQuantityPerType(DeviceType.THERMOSTAT);

        assertEquals(0, result,
                String.format("expected amount was 2, but was returned %s", result));
    }

    @Test
    void getDeviceQuantityPerTypeEmptyTest() {
        when(storageMock.listAll()).thenReturn(List.of());

        int result = intelligentHomeCenter.getDeviceQuantityPerType(DeviceType.THERMOSTAT);

        assertEquals(0, result,
                String.format("expected amount was 0, but was returned %s", result));
    }

    @Test
    void getTopNDevicesByPowerConsumptionNegativeNTest() {
        assertThrows(IllegalArgumentException.class,
                () -> intelligentHomeCenter.getTopNDevicesByPowerConsumption(-1),
                "Given n is a negative number, IllegalArgumentException was expected but not found");
    }

    @Test
    void getTopNDevicesByPowerConsumptionEmptyStorageTest() {
        when(storageMock.listAll()).thenReturn(List.of());

        assertEquals(List.of(),
                intelligentHomeCenter.getTopNDevicesByPowerConsumption(3),
                "An empty list was expected, when the storage is empty, given any n >= 0");
    }

    @Test
    void getTopNDevicesByPowerConsumptionBasicTest() {
        IoTDevice top1 = new AmazonAlexa("top1", 37, LocalDateTime.now().minusHours(3));
        IoTDevice top2 = new AmazonAlexa("top2", 24, LocalDateTime.now().minusHours(3));
        IoTDevice top3 = new AmazonAlexa("top3", 11, LocalDateTime.now().minusHours(3));

        when(storageMock.listAll()).thenReturn(List.of(
                top1, top2,
                new AmazonAlexa("aa2", 2, LocalDateTime.now().minusHours(3)),
                new AmazonAlexa("aa1", 8, LocalDateTime.now().minusHours(3)),
                new RgbBulb("bulb1", 4, LocalDateTime.now().minusHours(3)),
                new WiFiThermostat("thermostat1", 7, LocalDateTime.now().minusHours(3)),
                top3
        ));

        assertEquals(List.of(top1.getId(), top2.getId(), top3.getId()),
                intelligentHomeCenter.getTopNDevicesByPowerConsumption(3),
                "Didn't return the expected device ids");
    }

    @Test
    void getTopNDevicesByPowerConsumptionBiggerNThanStorageTest() {
        when(storageMock.listAll()).thenReturn(List.of(
                new AmazonAlexa("top1", 24, LocalDateTime.now()),
                new AmazonAlexa("top2", 24, LocalDateTime.now()),
                new AmazonAlexa("top3", 24, LocalDateTime.now()),
                new AmazonAlexa("top4", 12, LocalDateTime.now()),
                new AmazonAlexa("top5", 12, LocalDateTime.now())
        ));

        int result = intelligentHomeCenter.getTopNDevicesByPowerConsumption(7).size();

        assertEquals(5, result,
                String.format("Expected the size to be 5, but it was %s", result));
    }

    @Test
    void getFirstNDevicesByRegistrationNegativeNTest() {
        assertThrows(IllegalArgumentException.class,
                () -> intelligentHomeCenter.getFirstNDevicesByRegistration(-1),
                "Expected IllegalArgumentException when n is negative");
    }

    @Test
    void getFirstNDevicesByRegistrationBasicTest() {
        IoTDevice device1 = new AmazonAlexa("top1", 24, LocalDateTime.now());
        device1.setRegistration(LocalDateTime.now());

        IoTDevice device2 = new AmazonAlexa("top2", 24, LocalDateTime.now());
        device2.setRegistration(LocalDateTime.now().plusHours(1));

        IoTDevice device3 = new AmazonAlexa("top3", 24, LocalDateTime.now());
        device3.setRegistration(LocalDateTime.now().plusHours(2));

        when(storageMock.listAll()).thenReturn(List.of(device1, device2, device3));

        assertEquals(List.of(device1, device2),
                intelligentHomeCenter.getFirstNDevicesByRegistration(2),
                "Didn't return the expected devices");
    }

    @Test
    void getFirstNDevicesByRegistrationNBiggerThanStorageTest() {
        IoTDevice device1 = new AmazonAlexa("top1", 24, LocalDateTime.now());
        device1.setRegistration(LocalDateTime.now());

        IoTDevice device2 = new AmazonAlexa("top2", 24, LocalDateTime.now());
        device2.setRegistration(LocalDateTime.now().plusHours(1));

        IoTDevice device3 = new AmazonAlexa("top3", 24, LocalDateTime.now());
        device3.setRegistration(LocalDateTime.now().plusHours(2));

        when(storageMock.listAll()).thenReturn(List.of(device1, device2, device3));

        assertEquals(List.of(device1, device2, device3),
                intelligentHomeCenter.getFirstNDevicesByRegistration(7),
                "Didn't return the expected devices");
    }
}
