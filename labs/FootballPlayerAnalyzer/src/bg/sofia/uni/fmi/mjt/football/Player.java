package bg.sofia.uni.fmi.mjt.football;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public record Player(String name, String fullName, LocalDate birthDate, int age, double heightCm, double weightKg,
                     List<Position> positions, String nationality, int overallRating, int potential, long valueEuro,
                     long wageEuro, Foot preferredFoot) {

    private static final String ATTRIBUTE_DELIMITER = ";";

    public static Player of(String line) {
        final String[] tokens = line.split(ATTRIBUTE_DELIMITER);

        return new Player(tokens[0], tokens[1],
                LocalDate.parse(tokens[2], DateTimeFormatter.ofPattern("M/d/yyyy")),
                Integer.parseInt(tokens[3]),
                Double.parseDouble(tokens[4]), Double.parseDouble(tokens[5]),
                Arrays.stream(tokens[6].split(","))
                        .map(positionStr -> {
                            try {
                                return Position.valueOf(positionStr);
                            } catch (IllegalArgumentException e) {
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()),
                tokens[7], Integer.parseInt(tokens[8]), Integer.parseInt(tokens[9]), Long.parseLong(tokens[10]),
                Long.parseLong(tokens[11]), Foot.valueOf(tokens[12].toUpperCase()));
    }
}
