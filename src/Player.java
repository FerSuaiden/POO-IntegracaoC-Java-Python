package src;
public class Player {
    private int id;
    private int age;
    private String playerName;
    private String nationality;
    private String clubName;

    public Player(int id, int age, String playerName, String nationality, String clubName) {
        this.id = id;
        this.age = age;
        this.playerName = playerName;
        this.nationality = nationality;
        this.clubName = clubName;
    }

    public int getId() {
        return id;
    }

    public int getAge() {
        return age;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getNationality() {
        return nationality;
    }

    public String getClubName() {
        return clubName;
    }

    @Override
    public String toString() {
        return String.format("ID: %d, Age: %d, Player: %s, Nationality: %s, Club: %s", id, age, playerName, nationality, clubName);
    }

    public String toCSVString() {
        return String.format("%d,%d,%s,%s,%s", id, age, playerName, nationality, clubName);
    }
}
