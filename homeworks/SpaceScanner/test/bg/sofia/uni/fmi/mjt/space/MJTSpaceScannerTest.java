package bg.sofia.uni.fmi.mjt.space;

import bg.sofia.uni.fmi.mjt.space.algorithm.Rijndael;
import bg.sofia.uni.fmi.mjt.space.algorithm.SymmetricBlockCipher;
import bg.sofia.uni.fmi.mjt.space.exception.CipherException;
import bg.sofia.uni.fmi.mjt.space.exception.TimeFrameMismatchException;
import bg.sofia.uni.fmi.mjt.space.mission.Mission;
import bg.sofia.uni.fmi.mjt.space.mission.MissionStatus;
import bg.sofia.uni.fmi.mjt.space.rocket.Rocket;
import bg.sofia.uni.fmi.mjt.space.rocket.RocketStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MJTSpaceScannerTest {

    private static SpaceScannerAPI spaceScannerAPI;
    private static SymmetricBlockCipher rijndael;

    @BeforeAll
    static void init() throws NoSuchAlgorithmException {
        String rocketsData = "\"\",Name,Wiki,Rocket Height" + System.lineSeparator() +
                "0,Tsyklon-3,https://en.wikipedia.org/wiki/Tsyklon-3,39.0 m" + System.lineSeparator() +
                "10,VLS-1,https://en.wikipedia.org/wiki/VLS-1,19.0 m" + System.lineSeparator() +
                "2,Unha-2,https://en.wikipedia.org/wiki/Unha,28.0 m" + System.lineSeparator() +
                "109,Ceres-1,,19.0 m";

        String missionsData = "Unnamed: 0,Company Name,Location,Datum,Detail,Status Rocket,\" Rocket\",Status Mission" + System.lineSeparator() +
                "0,VKS RF,\"Site 32/2, Plesetsk Cosmodrome, Russia\",\"Fri Jan 30, 2009\",Tsyklon-3 | Koronas Foton,StatusRetired,\"50.0\",Success" + System.lineSeparator() +
                "1,VKS RF,\"Site 32/2, Plesetsk Cosmodrome, Russia\",\"Fri Dec 24, 2004\",Tsyklon-3 | Sich 1M & Micron 1,StatusRetired,,Partial Failure" + System.lineSeparator() +
                "2,VKS RF,\"Site 32/1, Plesetsk Cosmodrome1, Russia\",\"Fri Dec 28, 2001\",Tsyklon-3 | Cosmos 2384 to 2386 & Gonets 10 to 12,StatusRetired,\"10.0\",Success" + System.lineSeparator() +
                "3,KCST,\"Pad 1, Tonghae Satellite Launching Ground, North Korea\",\"Sun Apr 05, 2009\",Unha-2 | Kwangmy\u0081\u008Fngs\u0081\u008Fng-2,StatusRetired,,Failure" + System.lineSeparator() +
                "4,AEB,\"VLS Pad, Alc?›ntara Launch Center, Maranh?œo, Brazil\",\"Mon Aug 25, 2003\",\"VLS-1 | SATEC, UNOSAT\",StatusActive,,Prelaunch Failure" + System.lineSeparator() +
                "5,AEB,\"VLS Pad, Alc?›ntara Launch Center, Maranh?œo, Brazil\",\"Sat Dec 11, 1999\",VLS-1 | SACI-2,StatusRetired,5.0,Failure" + System.lineSeparator() +
                "6,AEB,\"VLS Pad, Alc?›ntara Launch Center, Maranh?œo 2, Brazil\",\"Sun Nov 02, 1997\",VLS-1 | SCD-2A,StatusActive,,Success" + System.lineSeparator();

        BufferedReader rocketsReader = new BufferedReader(new StringReader(rocketsData));
        BufferedReader missionsReader = new BufferedReader(new StringReader(missionsData));

        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        SecretKey key = keyGen.generateKey();
        spaceScannerAPI = new MJTSpaceScanner(missionsReader, rocketsReader, key);
        rijndael = new Rijndael(key);
    }

    @Test
    void testGetAllMissions() {
        assertEquals(7, spaceScannerAPI.getAllMissions().size());
    }

    @Test
    void testGetAllSuccessfulMissions() {
        assertEquals(3, spaceScannerAPI.getAllMissions(MissionStatus.SUCCESS).size());
    }

    @Test
    void testGetAllMissionsWithNullStatus() {
        assertThrows(
                IllegalArgumentException.class,
                () -> spaceScannerAPI.getAllMissions(null));
    }

    @Test
    void testGetCompanyWithMostSuccessfulMissions() {
        assertEquals("VKS RF",
                spaceScannerAPI.getCompanyWithMostSuccessfulMissions(LocalDate.MIN, LocalDate.MAX));
    }

    @Test
    void testGetCompanyWithMostSuccessfulMissionsNullFromDate() {
        assertThrows(IllegalArgumentException.class,
                () -> spaceScannerAPI.getCompanyWithMostSuccessfulMissions(null, LocalDate.MAX));
    }

    @Test
    void testGetCompanyWithMostSuccessfulMissionsNullToDate() {
        assertThrows(IllegalArgumentException.class,
                () -> spaceScannerAPI.getCompanyWithMostSuccessfulMissions(LocalDate.MIN, null));
    }

    @Test
    void testGetCompanyWithMostSuccessfulMissionsFromAfterTo() {
        assertThrows(TimeFrameMismatchException.class,
                () -> spaceScannerAPI.getCompanyWithMostSuccessfulMissions(LocalDate.MAX, LocalDate.MIN));
    }

    @Test
    void testGetMissionsPerCountry() {
        Map<String, Collection<Mission>> missions = spaceScannerAPI.getMissionsPerCountry();

        assertEquals(3, missions.get("Russia").size());
        assertEquals(3, missions.get("Brazil").size());
        assertEquals(1, missions.get("North Korea").size());
    }

    @Test
    void testGetTopNLeastExpensiveMissionsBiggerN() {
        assertEquals(2,
                spaceScannerAPI.getTopNLeastExpensiveMissions(10, MissionStatus.SUCCESS, RocketStatus.STATUS_RETIRED).size()
        );
    }

    @Test
    void testGetTopNLeastExpensiveMissionsSmallerN() {
        List<Mission> missions = spaceScannerAPI.getTopNLeastExpensiveMissions(1, MissionStatus.SUCCESS, RocketStatus.STATUS_RETIRED);
        Mission mission = Mission.of("2,VKS RF,\"Site 32/1, Plesetsk Cosmodrome1, Russia\",\"Fri Dec 28, 2001\",Tsyklon-3 | Cosmos 2384 to 2386 & Gonets 10 to 12,StatusRetired,\"10.0\",Success");

        assertEquals(1, missions.size());
        assertEquals(mission, missions.get(0));
    }

    @Test
    void testGetTopNLeastExpensiveMissionsNullMissionStatus() {
        assertThrows(IllegalArgumentException.class,
                () -> spaceScannerAPI.getTopNLeastExpensiveMissions(1, null, RocketStatus.STATUS_RETIRED)
        );
    }

    @Test
    void testGetTopNLeastExpensiveMissionsNullRocketStatus() {
        assertThrows(IllegalArgumentException.class,
                () -> spaceScannerAPI.getTopNLeastExpensiveMissions(1, MissionStatus.SUCCESS, null)
        );
    }

    @Test
    void testGetTopNLeastExpensiveMissionsNegativeN() {
        assertThrows(IllegalArgumentException.class,
                () -> spaceScannerAPI.getTopNLeastExpensiveMissions(-1, MissionStatus.SUCCESS, RocketStatus.STATUS_RETIRED)
        );
    }

    @Test
    void testGetTopNLeastExpensiveMissionsZeroN() {
        assertThrows(IllegalArgumentException.class,
                () -> spaceScannerAPI.getTopNLeastExpensiveMissions(0, MissionStatus.SUCCESS, RocketStatus.STATUS_RETIRED)
        );
    }

    @Test
    void testGetMostDesiredLocationForMissionsPerCompany() {
        Map<String, String> companyLocations = spaceScannerAPI.getMostDesiredLocationForMissionsPerCompany();

        assertEquals("Site 32/2, Plesetsk Cosmodrome, Russia", companyLocations.get("VKS RF"));
        assertEquals("VLS Pad, Alc?›ntara Launch Center, Maranh?œo, Brazil", companyLocations.get("AEB"));
        assertEquals("Pad 1, Tonghae Satellite Launching Ground, North Korea", companyLocations.get("KCST"));
    }

    @Test
    void testGetLocationWithMostSuccessfulMissionsPerCompany() {
        Map<String, String> companyLocations = spaceScannerAPI.getLocationWithMostSuccessfulMissionsPerCompany(LocalDate.MIN, LocalDate.MAX);

        assertEquals("Site 32/2, Plesetsk Cosmodrome, Russia", companyLocations.get("VKS RF"));
        assertEquals("VLS Pad, Alc?›ntara Launch Center, Maranh?œo 2, Brazil", companyLocations.get("AEB"));
    }

    @Test
    void testGetLocationWithMostSuccessfulMissionsPerCompanyNullFrom() {
        assertThrows(IllegalArgumentException.class,
                () -> spaceScannerAPI.getLocationWithMostSuccessfulMissionsPerCompany(null, LocalDate.MAX));
    }

    @Test
    void testGetLocationWithMostSuccessfulMissionsPerCompanyNullTo() {
        assertThrows(IllegalArgumentException.class,
                () -> spaceScannerAPI.getLocationWithMostSuccessfulMissionsPerCompany(LocalDate.MIN, null));
    }

    @Test
    void testGetLocationWithMostSuccessfulMissionsPerCompanyFromAfterTo() {
        assertThrows(TimeFrameMismatchException.class,
                () -> spaceScannerAPI.getLocationWithMostSuccessfulMissionsPerCompany(LocalDate.MAX, LocalDate.MIN));
    }

    @Test
    void getAllRocketsTest() {
        Collection<Rocket> rockets = spaceScannerAPI.getAllRockets();
        Collection<Rocket> eqRockets = List.of(
                Rocket.of("0,Tsyklon-3,https://en.wikipedia.org/wiki/Tsyklon-3,39.0 m"),
                Rocket.of("10,VLS-1,https://en.wikipedia.org/wiki/VLS-1,19.0 m"),
                Rocket.of("2,Unha-2,https://en.wikipedia.org/wiki/Unha,28.0 m"),
                Rocket.of("109,Ceres-1,,19.0 m")
        );

        assertEquals(4, rockets.size());
        assertIterableEquals(eqRockets, rockets, "Rockets do not match");
    }

    @Test
    void testGetTopNTallestRocketsBiggerN() {
        List<Rocket> rockets = spaceScannerAPI.getTopNTallestRockets(10);
        Collection<Rocket> eqRockets = List.of(
                Rocket.of("0,Tsyklon-3,https://en.wikipedia.org/wiki/Tsyklon-3,39.0 m"),
                Rocket.of("2,Unha-2,https://en.wikipedia.org/wiki/Unha,28.0 m"),
                Rocket.of("10,VLS-1,https://en.wikipedia.org/wiki/VLS-1,19.0 m"),
                Rocket.of("109,Ceres-1,,19.0 m")
        );

        assertEquals(4, rockets.size());
        assertIterableEquals(eqRockets, rockets, "Rockets do not match");
    }

    @Test
    void testGetTopNTallestRocketsSmallerN() {
        List<Rocket> rockets = spaceScannerAPI.getTopNTallestRockets(2);
        Collection<Rocket> eqRockets = List.of(
                Rocket.of("0,Tsyklon-3,https://en.wikipedia.org/wiki/Tsyklon-3,39.0 m"),
                Rocket.of("2,Unha-2,https://en.wikipedia.org/wiki/Unha,28.0 m")
        );

        assertEquals(2, rockets.size());
        assertIterableEquals(eqRockets, rockets, "Rockets do not match");
    }

    @Test
    void testGetTopNTallestRocketsNegativeN() {
        assertThrows(IllegalArgumentException.class,
                () -> spaceScannerAPI.getTopNTallestRockets(-1));
    }

    @Test
    void testGetTopNTallestRocketsZeroN() {
        assertThrows(IllegalArgumentException.class,
                () -> spaceScannerAPI.getTopNTallestRockets(0));
    }

    @Test
    void testGetWikiPageForRocket() {
        Map<String, Optional<String>> wikis = spaceScannerAPI.getWikiPageForRocket();
        assertEquals(Optional.of("https://en.wikipedia.org/wiki/Tsyklon-3"), wikis.get("Tsyklon-3"));
        assertEquals(Optional.of("https://en.wikipedia.org/wiki/Unha"), wikis.get("Unha-2"));
        assertEquals(Optional.of("https://en.wikipedia.org/wiki/VLS-1"), wikis.get("VLS-1"));
        assertEquals(Optional.empty(), wikis.get("Ceres-1"));
    }

    @Test
    void testGetWikiPagesForRocketsUsedInMostExpensiveMissionsSuccess() {
        List<String> expensiveMissionsWikis = spaceScannerAPI
                .getWikiPagesForRocketsUsedInMostExpensiveMissions(1, MissionStatus.SUCCESS, RocketStatus.STATUS_RETIRED);

        assertEquals(1, expensiveMissionsWikis.size());
        assertEquals("https://en.wikipedia.org/wiki/Tsyklon-3", expensiveMissionsWikis.getFirst());
    }

    @Test
    void testGetWikiPagesForRocketsUsedInMostExpensiveMissionsBiggerN() {
        List<String> expensiveMissionsWikis = spaceScannerAPI
                .getWikiPagesForRocketsUsedInMostExpensiveMissions(10, MissionStatus.FAILURE, RocketStatus.STATUS_RETIRED);

        assertEquals(2, expensiveMissionsWikis.size());
        assertEquals("https://en.wikipedia.org/wiki/VLS-1", expensiveMissionsWikis.get(0));
        assertEquals("https://en.wikipedia.org/wiki/Unha", expensiveMissionsWikis.get(1));
    }

    @Test
    void testGetWikiPagesForRocketsUsedInMostExpensiveMissionsFailure() {
        List<String> expensiveMissionsWikis = spaceScannerAPI
                .getWikiPagesForRocketsUsedInMostExpensiveMissions(10, MissionStatus.FAILURE, RocketStatus.STATUS_RETIRED);

        assertEquals(2, expensiveMissionsWikis.size());
        assertEquals("https://en.wikipedia.org/wiki/VLS-1", expensiveMissionsWikis.get(0));
        assertEquals("https://en.wikipedia.org/wiki/Unha", expensiveMissionsWikis.get(1));
    }

    @Test
    void testGetWikiPagesForRocketsUsedInMostExpensiveMissionsNullMissionStatus() {
        assertThrows(IllegalArgumentException.class,
                () -> spaceScannerAPI
                        .getWikiPagesForRocketsUsedInMostExpensiveMissions(10, null, RocketStatus.STATUS_RETIRED));
    }

    @Test
    void testGetWikiPagesForRocketsUsedInMostExpensiveMissionsNullRocketStatus() {
        assertThrows(IllegalArgumentException.class,
                () -> spaceScannerAPI
                        .getWikiPagesForRocketsUsedInMostExpensiveMissions(10, MissionStatus.SUCCESS, null));
    }

    @Test
    void testGetWikiPagesForRocketsUsedInMostExpensiveMissionsNegativeN() {
        assertThrows(IllegalArgumentException.class,
                () -> spaceScannerAPI
                        .getWikiPagesForRocketsUsedInMostExpensiveMissions(-1, MissionStatus.SUCCESS, RocketStatus.STATUS_ACTIVE));
    }

    @Test
    void testGetWikiPagesForRocketsUsedInMostExpensiveMissionsZeroN() {
        assertThrows(IllegalArgumentException.class,
                () -> spaceScannerAPI
                        .getWikiPagesForRocketsUsedInMostExpensiveMissions(0, MissionStatus.SUCCESS, RocketStatus.STATUS_ACTIVE));
    }

    @Test
    void testSaveMostReliableRocket() throws Exception {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            spaceScannerAPI.saveMostReliableRocket(outputStream, LocalDate.MIN, LocalDate.MAX);

            try (ByteArrayInputStream encryptedInputStream = new ByteArrayInputStream(outputStream.toByteArray());
                 ByteArrayOutputStream decryptedOutputStream = new ByteArrayOutputStream()) {

                rijndael.decrypt(encryptedInputStream, decryptedOutputStream);

                assertEquals("Tsyklon-3", decryptedOutputStream.toString());
            }
        }
    }

    @Test
    void testSaveMostReliableRocketNullFrom() throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            assertThrows(IllegalArgumentException.class,
                    () -> spaceScannerAPI.saveMostReliableRocket(outputStream, null, LocalDate.MAX));
        }
    }

    @Test
    void testSaveMostReliableRocketNullTo() throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            assertThrows(IllegalArgumentException.class,
                    () -> spaceScannerAPI.saveMostReliableRocket(outputStream, LocalDate.MIN, null));
        }
    }

    @Test
    void testSaveMostReliableRocketFromAfterTo() throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            assertThrows(TimeFrameMismatchException.class,
                    () -> spaceScannerAPI.saveMostReliableRocket(outputStream, LocalDate.MAX, LocalDate.MIN));
        }
    }
}
