package bg.sofia.uni.fmi.mjt.simcity.utility;

import bg.sofia.uni.fmi.mjt.simcity.property.billable.Billable;

import java.util.EnumMap;
import java.util.Map;

public class UtilityService implements UtilityServiceAPI {
    private EnumMap<UtilityType, Double> taxRates;
    private final int floatingValue = 100;

    public UtilityService(Map<UtilityType, Double> taxRates) {
        this.taxRates = new EnumMap<>(taxRates);
    }

    private double formatDifference(double value) {
        return Math.abs(
                (double) Math.round(value * floatingValue) / floatingValue
        );
    }

    @Override
    public <T extends Billable> double getUtilityCosts(UtilityType utilityType, T billable) {
        if (billable == null || utilityType == null) {
            throw new IllegalArgumentException();
        }

        double consumption = switch (utilityType) {
            case WATER -> billable.getWaterConsumption();
            case ELECTRICITY -> billable.getElectricityConsumption();
            case NATURAL_GAS -> billable.getNaturalGasConsumption();
        };

        return taxRates.get(utilityType) * consumption;
    }

    @Override
    public <T extends Billable> Map<UtilityType, Double> computeCostsDifference(T firstBillable, T secondBillable) {
        if (firstBillable == null || secondBillable == null) {
            throw new IllegalArgumentException();
        }

        EnumMap<UtilityType, Double> differences = new EnumMap<>(UtilityType.class);

        differences.put(UtilityType.WATER,
                formatDifference(
                        taxRates.get(UtilityType.WATER) *
                                (firstBillable.getWaterConsumption() - secondBillable.getWaterConsumption())
                ));

        differences.put(UtilityType.ELECTRICITY,
                formatDifference(
                        taxRates.get(UtilityType.ELECTRICITY) *
                                (firstBillable.getElectricityConsumption() - secondBillable.getElectricityConsumption())
                ));

        differences.put(UtilityType.NATURAL_GAS,
                formatDifference(
                        taxRates.get(UtilityType.NATURAL_GAS) *
                                (firstBillable.getNaturalGasConsumption() - secondBillable.getNaturalGasConsumption())
                ));

        return Map.copyOf(differences);
    }

    @Override
    public <T extends Billable> double getTotalUtilityCosts(T billable) {
        if (billable == null) {
            throw new IllegalArgumentException();
        }

        return taxRates.get(UtilityType.WATER) * billable.getWaterConsumption() +
                taxRates.get(UtilityType.ELECTRICITY) * billable.getElectricityConsumption() +
                taxRates.get(UtilityType.NATURAL_GAS) * billable.getNaturalGasConsumption();
    }
}
