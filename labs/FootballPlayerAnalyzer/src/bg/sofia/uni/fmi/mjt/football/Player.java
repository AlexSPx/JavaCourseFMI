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

        int index = 0;

        return new Player(tokens[index++], tokens[index++],
                LocalDate.parse(tokens[index++], DateTimeFormatter.ofPattern("M/d/yyyy")),
                Integer.parseInt(tokens[index++]),
                Double.parseDouble(tokens[index++]), Double.parseDouble(tokens[index++]),
                Arrays.stream(tokens[index++].split(","))
                        .map(positionStr -> {
                            try {
                                return Position.valueOf(positionStr);
                            } catch (IllegalArgumentException e) {
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()),
                tokens[index++], Integer.parseInt(tokens[index++]), Integer.parseInt(tokens[index++]),
                Long.parseLong(tokens[index++]), Long.parseLong(tokens[index++]),
                Foot.valueOf(tokens[index++].toUpperCase()));
    }
}
