package victorvld.gameofthree.domain.entities;

public enum GameMode {

    AUTO("auto"),
    MANUAL("manual");

    private final String mode;

    GameMode(String mode) {
        this.mode = mode;
    }

    public String getMode() {
        return mode;
    }

    public static GameMode of(String value) {
        for (GameMode gameMode : GameMode.values()) {
            if (gameMode.mode.equalsIgnoreCase(value)) {
                return gameMode;
            }
        }
        throw new IllegalArgumentException("Invalid mode: " + value);
    }
}
