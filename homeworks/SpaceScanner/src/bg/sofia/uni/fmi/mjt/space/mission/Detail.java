package bg.sofia.uni.fmi.mjt.space.mission;

public record Detail(String rocketName, String payload) {
    public static Detail of(String data) {
        String[] tokens = data.split("\\|");
        return new Detail(tokens[0].trim(), tokens[1].trim());
    }
}
