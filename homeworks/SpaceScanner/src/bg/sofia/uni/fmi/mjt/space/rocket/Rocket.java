package bg.sofia.uni.fmi.mjt.space.rocket;

import java.util.Optional;

public record Rocket(String id, String name, Optional<String> wiki, Optional<Double> height) {
    private static final int ID_INDEX = 0;
    private static final int NAME_INDEX = 1;
    private static final int WIKI_LINK_INDEX = 2;
    private static final int HEIGHT_INDEX = 3;

    public static Rocket of(String data) {
        String[] tokens = data.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);

        for (int i = 0; i < tokens.length; i++) {
            tokens[i] = tokens[i].replaceAll("\"", "").trim();
        }

        return new Rocket(
                tokens[ID_INDEX],
                tokens[NAME_INDEX],
                tokens[WIKI_LINK_INDEX].isEmpty()
                        ? Optional.empty()
                        : Optional.of(tokens[WIKI_LINK_INDEX]),
                tokens[HEIGHT_INDEX].trim().isEmpty()
                        ? Optional.empty()
                        : Optional.of(Double.parseDouble(tokens[HEIGHT_INDEX].replace("m", "")))
        );
    }

}
