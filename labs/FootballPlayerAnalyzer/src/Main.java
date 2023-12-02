import bg.sofia.uni.fmi.mjt.football.FootballPlayerAnalyzer;
import bg.sofia.uni.fmi.mjt.football.Position;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {

        FileReader reader = new FileReader("fifa_players_clean.csv");
        FootballPlayerAnalyzer analyzer = new FootballPlayerAnalyzer(reader);

        System.out.println(analyzer.getTopProspectPlayerForPositionInBudget(Position.CB, 400000000));
    }
}
