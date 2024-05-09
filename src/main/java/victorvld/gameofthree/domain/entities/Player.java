package victorvld.gameofthree.domain.entities;

public enum Player {
    PLAYER1("player1"),
    PLAYER2("player2");
    private final String name;

    Player(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Player of(String value) {
        for (Player player : Player.values()) {
            if (player.name.equalsIgnoreCase(value)) {
                return player;
            }
        }
        throw new IllegalArgumentException("Invalid player name: " + value);
    }
    public Player opposite() {
        if (this.equals(PLAYER1)) {
            return PLAYER2;
        } else {
            return PLAYER1;
        }
    }
}
