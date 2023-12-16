package bg.sofia.uni.fmi.mjt.football;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class FootballPlayerAnalyzerTest {

    private static FootballPlayerAnalyzer analyzer;

    @BeforeAll
    void init() {
        String document = "name;full_name;birth_date;age;height_cm;weight_kgs;positions;nationality;overall_rating;potential;value_euro;wage_euro;preferred_foot" + System.lineSeparator() +
                "L. Messi;Lionel Andrés Messi Cuccittini;6/24/1987;31;170.18;72.1;CF,RW,ST;Argentina;94;94;110500000;565000;Left" + System.lineSeparator() +
                "C. Eriksen;Christian Dannemann Eriksen;2/14/1992;27;154.94;76.2;CAM,RM,CM;Denmark;88;89;69500000;205000;Right" + System.lineSeparator() +
                "P. Pogba;Paul Pogba;3/15/1993;25;190.5;83.9;CM,CAM;France;88;91;73000000;255000;Right" + System.lineSeparator() +
                "L. Insigne;Lorenzo Insigne;6/4/1991;27;162.56;59;LW,ST;Italy;88;88;62000000;165000;Right" + System.lineSeparator() +
                "K. Koulibaly;Kalidou Koulibaly;6/20/1991;27;187.96;88.9;CB;Senegal;88;91;60000000;135000;Right" + System.lineSeparator() +
                "V. van Dijk;Virgil van Dijk;7/8/1991;27;193.04;92.1;CB;Netherlands;88;90;59500000;215000;Right" + System.lineSeparator() +
                "K. Mbappé;Kylian Mbappé;12/20/1998;20;152.4;73;RW,ST,RM;France;88;95;81000000;100000;Right" + System.lineSeparator() +
                "S. Agüero;Sergio Leonel Agüero del Castillo;6/2/1988;30;172.72;69.9;ST;Argentina;89;89;64500000;300000;Right" + System.lineSeparator() +
                "M. Neuer;Manuel Neuer;3/27/1986;32;193.04;92.1;GK;Germany;89;89;38000000;130000;Right" + System.lineSeparator() +
                    "M. ter Stegen;Marc-André ter Stegen;4/30/1992;26;187.96;84.8;GK;Germany;89;92;58000000;240000;Right";

        BufferedReader reader = new BufferedReader(new StringReader(document));
        analyzer = new FootballPlayerAnalyzer(reader);
    }

    @Test
    void testCheckLoadCorrectness() {
        assertEquals(analyzer.getAllPlayers().size(), 10, "Not all players were loaded");
    }

    @Test
    void testGetHighestPaidPlayerByNationality() {
        assertEquals(analyzer.getHighestPaidPlayerByNationality("Germany"),
                Player.of("M. ter Stegen;Marc-André ter Stegen;4/30/1992;26;187.96;84.8;GK;Germany;89;92;58000000;240000;Right"),
                "Players do not match");
    }

    @Test
    void testGetAllNationalities() {
        assertEquals(analyzer.getAllNationalities(),
                Set.of("Argentina", "Denmark", "France", "Italy", "Senegal", "Netherlands", "Germany"),
                "nationalities does not match");
    }

    @Test
    void testGroupByPosition() {
        Map<Position, Set<Player>> positionMap = analyzer.groupByPosition();
        assertEquals(9, positionMap.size());
        assertTrue(positionMap.containsKey(Position.CF));
        assertTrue(positionMap.containsKey(Position.RW));
        assertTrue(positionMap.containsKey(Position.CB));
        assertTrue(positionMap.containsKey(Position.GK));
    }

    @Test
    void testGetTopProspectPlayerForPositionInBudget() {
        Optional<Player> topProspect = analyzer.getTopProspectPlayerForPositionInBudget(Position.ST, 70000000);
        assertTrue(topProspect.isPresent());
        assertEquals("L. Insigne", topProspect.get().name());
    }

    @Test
    void testGetTopProspectPlayerForPositionInBudgetWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class,
                () -> analyzer.getTopProspectPlayerForPositionInBudget(null, 50000000));
        assertThrows(IllegalArgumentException.class,
                () -> analyzer.getTopProspectPlayerForPositionInBudget(Position.ST, -100000));
    }

    @Test
    void testGetSimilarPlayers() {
        Player samplePlayer = Player.of("Test Player;Test Player;1/1/1990;28;180.0;75.0;ST;Test;85;90;50000000;200000;Right");

        assertEquals(2, analyzer.getSimilarPlayers(samplePlayer).size());
    }

    @Test
    void testGetPlayersByFullNameKeyword() {
        Set<Player> playersByKeyword = analyzer.getPlayersByFullNameKeyword("Messi");
        assertEquals(1, playersByKeyword.size());
        assertEquals("L. Messi", playersByKeyword.iterator().next().name());
    }

    @Test
    void testGetPlayersByFullNameKeywordWithNullKeyword() {
        assertThrows(IllegalArgumentException.class, () -> analyzer.getPlayersByFullNameKeyword(null));
    }

    @Test
    void testGetHighestPaidPlayerByNationalityNull() {
        assertThrows(IllegalArgumentException.class, () -> analyzer.getHighestPaidPlayerByNationality(null));
    }

    @Test
    void testGetSimilarPlayersNull() {
        assertThrows(IllegalArgumentException.class, () -> analyzer.getSimilarPlayers(null));
    }
}
