package src;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {

    public static List<Player> loadPlayersFromFile(String filePath) {
        List<Player> players = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                int id = Integer.parseInt(parts[0]);
                int age = Integer.parseInt(parts[1]);
                String playerName = parts[2];
                String nationality = parts[3];
                String clubName = parts[4];
                players.add(new Player(id, age, playerName, nationality, clubName));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return players;
    }

    public static void savePlayersToFile(String filePath, List<Player> players) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (Player player : players) {
                bw.write(player.toCSVString());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
