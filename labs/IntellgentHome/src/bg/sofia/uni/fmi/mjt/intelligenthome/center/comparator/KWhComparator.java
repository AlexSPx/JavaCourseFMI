package bg.sofia.uni.fmi.mjt.intelligenthome.center.comparator;

import bg.sofia.uni.fmi.mjt.intelligenthome.device.IoTDevice;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;

public class KWhComparator implements Comparator<IoTDevice> {
    public long powerConsumptionKWh(IoTDevice device) {
        long duration = Duration.between(device.getInstallationDateTime(), LocalDateTime.now()).toHours();
        return (long) (duration * device.getPowerConsumption());
    }

    @Override
    public int compare(IoTDevice firstDevice, IoTDevice secondDevice) {
        return Long.compare(powerConsumptionKWh(secondDevice), powerConsumptionKWh(firstDevice));
    }
}
