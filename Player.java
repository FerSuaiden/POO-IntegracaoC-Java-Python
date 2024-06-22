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

    // Getters
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

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public String toCSVString() {
        return id + "," + age + "," + playerName + "," + nationality + "," + clubName;
    }

    @Override
    public String toString() {
        return playerName + " (" + clubName + ")";
    }
}
