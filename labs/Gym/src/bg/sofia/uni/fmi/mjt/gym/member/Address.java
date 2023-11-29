package bg.sofia.uni.fmi.mjt.gym.member;

public record Address(double longitude, double latitude) {

    public double getDistanceTo(Address other) {
        return Math.sqrt(
                (Math.pow((longitude - other.longitude), 2) +
                        Math.pow((latitude - other.latitude), 2)
        ));
    }

}
