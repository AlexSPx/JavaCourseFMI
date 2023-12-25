package bg.sofia.uni.fmi.mjt.space.mission;

import bg.sofia.uni.fmi.mjt.space.rocket.RocketStatus;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public record Mission(String id, String company, String location, LocalDate date, Detail detail,
                      RocketStatus rocketStatus, Optional<Double> cost, MissionStatus missionStatus) {
    private static final int ID_INDEX = 0;
    private static final int COMPANY_INDEX = 1;
    private static final int LOCATION_INDEX = 2;
    private static final int DATE_INDEX = 3;
    private static final int DETAIL_INDEX = 4;
    private static final int ROCKET_STATUS_INDEX = 5;
    private static final int COST_INDEX = 6;
    private static final int MISSION_STATUS_INDEX = 7;

    public static Mission of(String data) {
        String[] tokens = data.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

        for (int i = 0; i < tokens.length; i++) {
            tokens[i] = tokens[i].replaceAll("\"", "").trim();
        }

        return new Mission(
                tokens[ID_INDEX],
                tokens[COMPANY_INDEX],
                tokens[LOCATION_INDEX],
                LocalDate.parse(tokens[DATE_INDEX], DateTimeFormatter.ofPattern("EEE MMM dd, yyyy")),
                Detail.of(tokens[DETAIL_INDEX]),
                RocketStatus.valueOfLabel(tokens[ROCKET_STATUS_INDEX]),
                tokens[COST_INDEX].trim().isEmpty()
                    ? Optional.empty()
                    : Optional.of(Double.parseDouble(tokens[COST_INDEX].replaceAll(",", ""))),
                MissionStatus.valueOfLabel(tokens[MISSION_STATUS_INDEX])
        );
    }

    public String getCountry() {
        String[] tokens = this.location().split(",");
        return tokens[tokens.length - 1].trim();
    }
}
