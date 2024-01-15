package bg.sofia.uni.fmi.mjt.space;

import bg.sofia.uni.fmi.mjt.space.algorithm.Rijndael;
import bg.sofia.uni.fmi.mjt.space.algorithm.SymmetricBlockCipher;
import bg.sofia.uni.fmi.mjt.space.exception.CipherException;
import bg.sofia.uni.fmi.mjt.space.exception.TimeFrameMismatchException;
import bg.sofia.uni.fmi.mjt.space.mission.Mission;
import bg.sofia.uni.fmi.mjt.space.mission.MissionStatus;
import bg.sofia.uni.fmi.mjt.space.mission.comparator.HighestCostComparator;
import bg.sofia.uni.fmi.mjt.space.mission.comparator.LeastCostComparator;
import bg.sofia.uni.fmi.mjt.space.rocket.Rocket;
import bg.sofia.uni.fmi.mjt.space.rocket.RocketStatus;
import bg.sofia.uni.fmi.mjt.space.rocket.comparator.RocketHeightComparator;

import javax.crypto.SecretKey;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Collection;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MJTSpaceScanner implements SpaceScannerAPI {
    private final List<Mission> missions;
    private final List<Rocket> rockets;
    private final SecretKey secretKey;

    public MJTSpaceScanner(Reader missionsReader, Reader rocketsReader, SecretKey secretKey) {
        try (var missionsBuffReader = new BufferedReader(missionsReader);
             var rocketsBuffReader = new BufferedReader(rocketsReader)
        ) {
            missionsBuffReader.readLine();
            rocketsBuffReader.readLine();

            this.missions = missionsBuffReader.lines().map(Mission::of).toList();
            this.rockets = rocketsBuffReader.lines().map(Rocket::of).toList();
            this.secretKey = secretKey;
        } catch (IOException e) {
            throw new UncheckedIOException("A problem occurred while reading from the file", e);
        }
    }

    @Override
    public Collection<Mission> getAllMissions() {
        return missions;
    }

    @Override
    public Collection<Mission> getAllMissions(MissionStatus missionStatus) {
        if (missionStatus == null) {
            throw new IllegalArgumentException("missionStatus cannot be null");
        }

        return missions.stream()
                .filter(mission -> mission.missionStatus() == missionStatus)
                .toList();
    }

    @Override
    public String getCompanyWithMostSuccessfulMissions(LocalDate from, LocalDate to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Date must not be null");
        }

        if (from.isAfter(to)) {
            throw new TimeFrameMismatchException("from date must be before to date");
        }

        return missions.stream()
                .filter(mission -> mission.date().isBefore(to) &&
                        mission.date().isAfter(from))
                .collect(
                        Collectors.groupingBy(Mission::company, Collectors.counting())
                )
                .entrySet()
                .stream()
                .max(Entry.comparingByValue())
                .map(Entry::getKey).orElse(null);
    }

    @Override
    public Map<String, Collection<Mission>> getMissionsPerCountry() {
        return missions.stream()
                .collect(
                        Collectors.groupingBy(
                                Mission::getCountry,
                                Collectors.mapping(Function.identity(), Collectors.toCollection(LinkedList::new))
                        )
                );
    }

    @Override
    public List<Mission> getTopNLeastExpensiveMissions(int n, MissionStatus missionStatus, RocketStatus rocketStatus) {
        if (n <= 0 || missionStatus == null || rocketStatus == null) {
            throw new IllegalArgumentException("n must be >0 and the status must not be null");
        }

        return missions.stream()
                .filter(mission -> mission.missionStatus() == missionStatus &&
                        mission.rocketStatus() == rocketStatus)
                .sorted(new LeastCostComparator())
                .limit(n)
                .toList();
    }

    @Override
    public Map<String, String> getMostDesiredLocationForMissionsPerCompany() {
        return missions.stream()
                .collect(
                        Collectors.groupingBy(
                                Mission::company,
                                Collectors.collectingAndThen(
                                        Collectors.groupingBy(
                                                Mission::location,
                                                Collectors.counting()
                                        ),
                                        countMap -> countMap.entrySet().stream()
                                                .max(Entry.comparingByValue())
                                                .map(Entry::getKey)
                                                .orElse("")
                                )
                        )
                );
    }

    @Override
    public Map<String, String> getLocationWithMostSuccessfulMissionsPerCompany(LocalDate from, LocalDate to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Dates must not be null");
        }

        if (from.isAfter(to)) {
            throw new TimeFrameMismatchException("from date must be before to date");
        }

        return missions.stream()
                .filter(mission -> mission.missionStatus() == MissionStatus.SUCCESS)
                .collect(
                        Collectors.groupingBy(
                                Mission::company,
                                Collectors.collectingAndThen(
                                        Collectors.groupingBy(
                                                Mission::location,
                                                Collectors.counting()
                                        ),
                                        countMap -> countMap.entrySet().stream()
                                                .max(Entry.comparingByValue())
                                                .map(Entry::getKey)
                                                .orElse("")
                                )
                        )
                );
    }

    @Override
    public Collection<Rocket> getAllRockets() {
        return rockets;
    }

    @Override
    public List<Rocket> getTopNTallestRockets(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("N must be > 0");
        }

        return rockets.stream()
                .sorted(new RocketHeightComparator())
                .limit(n)
                .toList();
    }

    @Override
    public Map<String, Optional<String>> getWikiPageForRocket() {
        return rockets.stream()
                .collect(Collectors.toMap(
                        Rocket::name,
                        Rocket::wiki
                ));
    }

    @Override
    public List<String> getWikiPagesForRocketsUsedInMostExpensiveMissions(int n,
                                                                          MissionStatus missionStatus,
                                                                          RocketStatus rocketStatus) {
        if (n <= 0 || missionStatus == null || rocketStatus == null) {
            throw new IllegalArgumentException("N must be >0 and the status must not be null");
        }

        List<String> rocketNames = missions.stream()
                .filter(mission -> mission.missionStatus() == missionStatus &&
                        mission.rocketStatus() == rocketStatus)
                .sorted(new HighestCostComparator())
                .map(mission -> mission.detail().rocketName())
                .distinct()
                .limit(n)
                .toList();

        return rockets.stream()
                .filter(rocket -> rocketNames.contains(rocket.name()))
                .map(rocket -> rocket.wiki().orElse(""))
                .toList();
    }

    @Override
    public void saveMostReliableRocket(OutputStream outputStream, LocalDate from, LocalDate to) throws CipherException {
        if (outputStream == null || from == null || to == null) {
            throw new IllegalArgumentException("OutputStream or dates must not be null");
        }

        if (from.isAfter(to)) {
            throw new TimeFrameMismatchException("from date must be before to date");
        }

        SymmetricBlockCipher algorithm = new Rijndael(secretKey);

        String mostReliableRocket = getMostReliableRocket(from, to);

        algorithm.encrypt(new ByteArrayInputStream(mostReliableRocket.getBytes(StandardCharsets.UTF_8)), outputStream);
    }

    public String getMostReliableRocket(LocalDate from, LocalDate to) {
        Map<String, EnumMap<MissionStatus, Long>> missionsStatus = missions.stream()
                .filter(mission -> mission.date().isAfter(from) && mission.date().isBefore(to))
                .collect(
                        Collectors.groupingBy(
                                mission -> mission.detail().rocketName(),
                                Collectors.groupingBy(
                                        Mission::missionStatus,
                                        () -> new EnumMap<>(MissionStatus.class),
                                        Collectors.counting()
                                )
                        )
                );
        Map<String, Double> rocks = rockets.stream()
                .collect(
                        Collectors.toMap(
                                Rocket::name,
                                rocket -> {
                                    EnumMap<MissionStatus, Long> rocketRecord = missionsStatus.get(rocket.name());
                                    return rocketRecord == null ? 0D : calculateRocketReliability(rocketRecord);
                                }
                        )
                );

        return rocks
                .entrySet().stream().max(
                        Entry.comparingByValue()
                ).map(Entry::getKey)
                .orElse(null);
    }

    private Double calculateRocketReliability(EnumMap<MissionStatus, Long> missions) {
        return (2.0 * missions.getOrDefault(MissionStatus.SUCCESS, 0L)
                + (missions.getOrDefault(MissionStatus.FAILURE, 0L)
                + missions.getOrDefault(MissionStatus.PARTIAL_FAILURE, 0L)
                + missions.getOrDefault(MissionStatus.PRELAUNCH_FAILURE, 0L))
                / (2 * missions.values().size()));
    }
}
